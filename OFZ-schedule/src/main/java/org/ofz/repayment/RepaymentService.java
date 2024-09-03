package org.ofz.repayment;

import org.ofz.payment.Payment;
import org.ofz.payment.PaymentRepository;
import org.ofz.repayment.dto.AccountDepositRes;
import org.ofz.repayment.dto.RepaymentRes;
import org.ofz.repayment.exception.ExternalServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RepaymentService {

    private static final Logger logger = LoggerFactory.getLogger(RepaymentService.class);

    private final PaymentRepository paymentRepository;
    private final RepaymentHistoryRepository repaymentHistoryRepository;
    private final WebClient webClient;

    @Value("${webclient.base-url}")
    private String partnersUrl;

    @Autowired
    public RepaymentService(PaymentRepository paymentRepository,
                            RepaymentHistoryRepository repaymentHistoryRepository,
                            WebClient.Builder webClientBuilder) {
        this.paymentRepository = paymentRepository;
        this.repaymentHistoryRepository = repaymentHistoryRepository;
//        this.webClient = webClientBuilder.baseUrl("http://ec2-3-34-1-150.ap-northeast-2.compute.amazonaws.com").build();
        this.webClient = webClientBuilder.baseUrl(partnersUrl).build();
    }

    /**
     * 오늘 상환 대상자 조회
     *
     * @return 상환 대상 Payment 리스트
     */
    public List<Payment> findTodayRepaymentTargets() {
        int today = LocalDate.now().getDayOfMonth();
        logger.info("오늘 상환 대상자를 조회합니다. 날짜: {}", today);

        List<Payment> targets = paymentRepository.findByRepaymentDateOrOverdueDay(today);
        logger.info("총 {}명의 상환 대상자를 찾았습니다.", targets.size());

        return targets;
    }

    /**
     * 상환 대상 중 실제 빚이 있는 사람 필터링
     *
     * @param candidates 추정 상환 대상자 리스트
     * @return 실제 상환 대상자 리스트
     */
    public List<Payment> filterRepaymentTargets(List<Payment> candidates) {
        logger.info("실제 빚이 있는 상환 대상자를 필터링합니다.");
        List<Payment> filteredTargets = candidates.stream()
                .filter(payment -> payment.getPreviousMonthDebt() > 0)
                .collect(Collectors.toList());

        logger.info("실제 상환 대상자 수: {}", filteredTargets.size());
        return filteredTargets;
    }

    /**
     * 상환 가능 여부 확인
     *
     * @param payment 상환 대상자
     * @return 상환 가능 여부 ("SUCCESS", "PARTIAL", "FAILURE")
     */
    public String checkRepaymentAbility(Payment payment) {
        int accountDeposit = getAccountDeposit(payment.getRepaymentAccountNumber());
        logger.info("계좌 잔고 확인 완료: 계좌번호: {}, 잔고: {}", payment.getRepaymentAccountNumber(), accountDeposit);

        if (accountDeposit == 0) {
            logger.warn("상환 불가: 계좌 잔고가 0원입니다.");
            return "FAILURE";
        } else if (accountDeposit >= payment.getPreviousMonthDebt()) {
            logger.info("상환 가능: 잔고가 빚보다 많습니다.");
            return "SUCCESS";
        } else {
            logger.info("상환 일부 가능: 잔고가 빚보다 적습니다.");
            return "PARTIAL";
        }
    }

    /**
     * 계좌 잔고 조회
     *
     * @param accountNumber 계좌 번호
     * @return 계좌 잔고
     */
    private int getAccountDeposit(String accountNumber) {
        try {
            logger.info("계좌 잔고를 조회합니다. 계좌번호: {}", accountNumber);
            AccountDepositRes response = webClient.post()
                    .uri(partnersUrl+"/mydata/accounts/deposits")
                    .contentType(MediaType.APPLICATION_JSON) // Content-Type 헤더 설정
                    .bodyValue("{\"accountNumber\": \"" + accountNumber + "\"}")
                    .retrieve()
                    .bodyToMono(AccountDepositRes.class)  // DTO 클래스로 변환
                    .block();

            int deposit = response.getDeposit();  // 응답에서 잔고 추출
            logger.info("계좌 잔고 조회 완료: 잔고: {}", deposit);
            return deposit;
        } catch (WebClientResponseException ex) {
            logger.error("계좌 잔고 조회 실패: 계좌번호: {}, 오류 메시지: {} - 상태 코드: {}",
                    accountNumber, ex.getMessage(), ex.getStatusCode());
            throw new ExternalServiceException("계좌 잔고 조회에 실패했습니다.", ex);
        } catch (Exception ex) {
            logger.error("계좌 잔고 조회 중 예상치 못한 오류 발생: 계좌번호: {}, 오류 메시지: {}",
                    accountNumber, ex.getMessage());
            throw new ExternalServiceException("계좌 잔고 조회 중 오류가 발생했습니다.", ex);
        }
    }



    /**
     * 계좌 잔고 차감
     *
     * @param accountNumber 계좌 번호
     * @param value         차감할 금액
     */
    private void debitAccount(String accountNumber, int value) {
        try {
            logger.info("계좌에서 금액을 차감합니다. 계좌번호: {}, 차감할 금액: {}", accountNumber, value);
            webClient.put()
                    .uri(partnersUrl + "/mydata/accounts/withdraw")
                    .contentType(MediaType.APPLICATION_JSON) // Content-Type 헤더 설정
                    .bodyValue("{\"accountNumber\": \"" + accountNumber + "\", \"value\": " + value + "}")
                    .retrieve()
                    .bodyToMono(Void.class)
                    .block();
            logger.info("계좌 차감 완료: 계좌번호: {}, 차감 금액: {}", accountNumber, value);
        } catch (WebClientResponseException ex) {
            logger.error("계좌 잔고 차감 실패: 계좌번호: {}, 오류 메시지: {} - 상태 코드: {}",
                    accountNumber, ex.getMessage(), ex.getStatusCode());
            throw new ExternalServiceException("계좌 잔고 차감에 실패했습니다.", ex);
        } catch (Exception ex) {
            logger.error("계좌 잔고 차감 중 예상치 못한 오류 발생: 계좌번호: {}, 오류 메시지: {}",
                    accountNumber, ex.getMessage());
            throw new ExternalServiceException("계좌 잔고 차감 중 오류가 발생했습니다.", ex);
        }
    }

    /**
     * 상환 성공 처리
     *
     * @param payment 상환 대상자
     */
    @Transactional
    public void processSuccessfulRepayment(Payment payment) {
        logger.info("상환 성공 처리를 시작합니다. 대상자 ID: {}", payment.getId());
        int repaymentAmount = payment.getPreviousMonthDebt();
        debitAccount(payment.getRepaymentAccountNumber(), repaymentAmount);
        payment.minusPreviousMonthDebt(repaymentAmount);
        logger.info("상환 성공: 대상자 ID: {}, 상환 금액: {}", payment.getId(), repaymentAmount);

        // RepaymentHistory 기록
        RepaymentHistory repaymentHistory = RepaymentHistory.builder()
                .userId(payment.getUser().getId())
                .repaymentAmount(repaymentAmount)
                .createdAt(LocalDateTime.now())
                .build();

        repaymentHistoryRepository.save(repaymentHistory);

        // 연체일 초기화
        payment.updateOverdueDay(null);
        payment.enablePay();
        paymentRepository.save(payment);
        logger.info("상환 내역 기록 완료: 대상자 ID: {}", payment.getUser().getId());
    }

    /**
     * 상환 일부 성공 처리
     *
     * @param payment 상환 대상자
     */
    @Transactional
    public void processPartialRepayment(Payment payment) {
        logger.info("상환 일부 처리를 시작합니다. 대상자 ID: {}", payment.getId());
        int accountDeposit = getAccountDeposit(payment.getRepaymentAccountNumber());
        debitAccount(payment.getRepaymentAccountNumber(), accountDeposit);

        int partialRepaymentAmount = accountDeposit;
        payment.minusPreviousMonthDebt(accountDeposit);

        payment.disablePay();

        if (payment.getOverdueDay() == null) {
            payment.updateOverdueDay(LocalDate.now());
        }
        paymentRepository.save(payment);
        logger.info("상환 일부 완료: 대상자 ID: {}, 상환 금액: {}", payment.getId(), partialRepaymentAmount);

        // RepaymentHistory 기록
        RepaymentHistory repaymentHistory = RepaymentHistory.builder()
                .userId(payment.getUser().getId())
                .repaymentAmount(partialRepaymentAmount)
                .createdAt(LocalDateTime.now())
                .build();

        repaymentHistoryRepository.save(repaymentHistory);
        logger.info("상환 내역 기록 완료: 대상자 ID: {}", payment.getUser().getId());
    }

    /**
     * 상환 실패 처리
     *
     * @param payment 상환 대상자
     */
    @Transactional
    public void processFailedRepayment(Payment payment) {
        logger.info("상환 실패 처리를 시작합니다. 대상자 ID: {}", payment.getId());
        payment.disablePay(); // 서비스 중지
        if (payment.getOverdueDay() == null) {
            payment.updateOverdueDay(LocalDate.now());
        }
        paymentRepository.save(payment);
        logger.warn("상환 실패 처리 완료: 대상자 ID: {}, 서비스 중지 및 연체일 설정", payment.getId());
    }

    /**
     * 전체 상환 프로세스 실행
     *
     * @return RepaymentResponse 결과 정보
     */

    public RepaymentRes processRepayments() {
        logger.info("전체 상환 프로세스를 시작합니다.");
        List<Payment> candidates = findTodayRepaymentTargets();
        List<Payment> repaymentTargets = filterRepaymentTargets(candidates);

        for (Payment payment : repaymentTargets) {
            String result = checkRepaymentAbility(payment);

            switch (result) {
                case "SUCCESS":
                    processSuccessfulRepayment(payment);
                    break;
                case "PARTIAL":
                    processPartialRepayment(payment);
                    break;
                case "FAILURE":
                    processFailedRepayment(payment);
                    break;
            }
        }
        logger.info("상환 프로세스가 완료되었습니다. 총 처리 대상: {}명", repaymentTargets.size());

        return new RepaymentRes("상환 프로세스가 완료되었습니다.", repaymentTargets.size());
    }
}
