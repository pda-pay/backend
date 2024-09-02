package org.ofz.management.service;

import org.ofz.management.dto.*;
import org.ofz.management.entity.MortgagedStock;
import org.ofz.management.entity.StockInformation;
import org.ofz.management.entity.StockPriority;
import org.ofz.management.exception.StockInformationNotFoundException;
import org.ofz.management.exception.UserNotFoundException;
import org.ofz.management.repository.MortgagedStockRepository;
import org.ofz.management.repository.StockInformationRepository;
import org.ofz.management.repository.StockPriorityRepository;
import org.ofz.management.repository.StockRepository;
import org.ofz.payment.entity.Payment;
import org.ofz.payment.exception.payment.PaymentNotFoundException;
import org.ofz.payment.repository.PaymentRepository;
import org.ofz.user.NameAndPhoneNumberProjection;
import org.ofz.user.User;
import org.ofz.user.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ManagementService {
    private final UserRepository userRepository;
    private final StockRepository stockRepository;
    private final MortgagedStockRepository mortgagedStockRepository;
    private final StockPriorityRepository stockPriorityRepository;
    private final PaymentRepository paymentRepository;
    private final StockInformationRepository stockInformationRepository;
    private final WebClient  webClient;

    @Value("${webclient.base-url}")
    private String baseUrl;

    public ManagementService(UserRepository userRepository,
                             StockRepository stockRepository,
                             MortgagedStockRepository mortgagedStockRepository,
                             StockPriorityRepository stockPriorityRepository,
                             PaymentRepository paymentRepository,
                             StockInformationRepository stockInformationRepository,
                             WebClient webClient) {
        this.userRepository = userRepository;
        this.stockRepository = stockRepository;
        this.mortgagedStockRepository = mortgagedStockRepository;
        this.stockPriorityRepository = stockPriorityRepository;
        this.paymentRepository = paymentRepository;
        this.stockInformationRepository = stockInformationRepository;
        this.webClient = webClient;
    }
    @Transactional
    public UserStockResponses getUserStocks(String userLoginId) {
        Long userId = findUserbyLoginId(userLoginId).getId();

        List<UserStockResponse> userStockResponses = new ArrayList<>();
        List<UserStockProjection> UserStockProjections = stockRepository.findUserStocksByUserId(userId);

        for (UserStockProjection userStockProjection : UserStockProjections) {
            final String stockCode = userStockProjection.getStockCode();
            // TODO: 전일 종가 요청하는 부분 캐시 완료 후 캐시 데이터 가져오는 식으로 대체
            final int previousStockPrice = fetchPreviousStockPrice(stockCode);
            System.out.println(stockCode);
            StockInformation stockInformation = stockInformationRepository.findByStockCode(stockCode)
                    .orElseThrow(() -> new StockInformationNotFoundException("증권 정보를 찾지 못했음."));

            UserStockResponse userStockresponse = new UserStockResponse(userStockProjection, previousStockPrice, stockInformation);
            userStockResponses.add(userStockresponse);
        }

        int totalDebt = paymentRepository.findTotalDebtByUserId(userId);

        return new UserStockResponses(userStockResponses, totalDebt);
    }

    @Transactional
    public UserAccountsResponse getUserAccounts(String userId) {
        NameAndPhoneNumberProjection nameAndPhoneNumberProjection = userRepository.findNameAndPhoneNumberByLoginId(userId)
                .orElseThrow(() -> new UserNotFoundException("사용자를 찾지 못했습니다."));
        UserAccountsRequest userAccountsRequest = new UserAccountsRequest(nameAndPhoneNumberProjection.getName(), nameAndPhoneNumberProjection.getPhoneNumber());
        return fetchUserAccounts(userAccountsRequest);
    }

    @Transactional
    public PaymentsJoinResponse joinPaymentsService(PaymentsJoinRequest paymentsJoinRequest) {
        User user = findUserbyLoginId(paymentsJoinRequest.getUserId());
        savePayment(paymentsJoinRequest, user);
        saveMortgagedStocks(paymentsJoinRequest.getMortgagedStocks(), user);
        savePriorityStocks(paymentsJoinRequest.getPriorityStocks(), user);
        return PaymentsJoinResponse.success(user.getLoginId());
    }

    @Transactional
    public UserCreditLimitResponse getUserCreditLimit(UserCreditLimitRequest userCreditLimitRequest) {
        User user = findUserbyLoginId(userCreditLimitRequest.getUserId());
        int currentCreditLimit = paymentRepository.findCreditLimitByUserId(user.getId())
                .orElseThrow(() -> new PaymentNotFoundException("결제 서비스 이용자를 찾지 못했습니다."));
        // TODO: 담보 총액 요청하는 부분 캐시 완료 후 캐시 데이터 가져오는 식으로 대체
        return new UserCreditLimitResponse(currentCreditLimit, 0, 0);
    }

    @Transactional
    public UpdateCreditLimitResponse updateUserCreditLimit(UpdateCreditLimitRequest updateCreditRequest) {
        User user = findUserbyLoginId(updateCreditRequest.getUserId());
        Payment payment = getPaymentByUserId(user.getId());
        payment.changeCreditLimit(updateCreditRequest.getCreditLimit());
        paymentRepository.save(payment);
        return UpdateCreditLimitResponse.success(updateCreditRequest.getUserId());
    }

    @Transactional
    public UpdateMortgagedStockResponse updateUserMortgagedStock(UpdateMortgagedStockResquest updateMortgagedStockResquest) {
        User user = findUserbyLoginId(updateMortgagedStockResquest.getUserId());
        List<MortgagedStock> mortgagedStocks = mortgagedStockRepository.findAllMortgagedStocksByUserId(user.getId());
        mortgagedStockRepository.deleteAll(mortgagedStocks);
        saveMortgagedStocks(updateMortgagedStockResquest.getMortgagedStocks(), user);

        List<StockPriority> stockPriorities = stockPriorityRepository.findAllStockPrioritiesByUserId(user.getId());
        stockPriorityRepository.deleteAll(stockPriorities);
        savePriorityStocks(updateMortgagedStockResquest.getPriorityStocks(), user);

        return UpdateMortgagedStockResponse.success(updateMortgagedStockResquest.getUserId());
    }


    @Transactional
    public UpdateRepaymentAccountResponse updateUserRepaymentAccount(UpdateRepaymentAccountRequest updateRepaymentAccountRequest) {
        User user = findUserbyLoginId(updateRepaymentAccountRequest.getUserId());
        Payment payment = getPaymentByUserId(user.getId());
        payment.changeRepaymentAccountNumber(updateRepaymentAccountRequest.getRepaymentAccountNumber());
        paymentRepository.save(payment);
        return UpdateRepaymentAccountResponse.success(updateRepaymentAccountRequest.getUserId());
    }

    private Payment getPaymentByUserId(Long userId) {
        return paymentRepository.findPaymentByUserId(userId)
                .orElseThrow(() -> new PaymentNotFoundException("결제 서비스 이용자를 찾지 못했습니다."));
    }

    private User findUserbyLoginId(String userId) {
        return userRepository.findByLoginId(userId)
                .orElseThrow(() -> new UserNotFoundException("사용자를 찾지 못했습니다."));
    }

    private int fetchPreviousStockPrice(String stockCode) {
        Mono<PreviousStockPriceResponse> previousStockPriceResponseMono = webClient.get()
                .uri("{baseUrl}/securities/stocks/{stockCode}", baseUrl, stockCode)
                .retrieve()
                .bodyToMono(PreviousStockPriceResponse.class);
        PreviousStockPriceResponse previousStockPriceResponse = previousStockPriceResponseMono.block();

        return previousStockPriceResponse.getAmount();
    }


    private UserAccountsResponse fetchUserAccounts(UserAccountsRequest userAccountsRequest) {
        UserAccountsResponse response = webClient.post()
                .uri("{baseUrl}/mydata/accounts", baseUrl)
                .bodyValue(userAccountsRequest)
                .retrieve()
                .bodyToMono(UserAccountsResponse.class)
                .block();

        if (response != null && response.getAccounts() != null) {
            List<UserAccountResponse> filteredAccounts = response.getAccounts().stream()
                    .filter(account -> "01".equals(account.getCategory()))
                    .collect(Collectors.toList());

            UserAccountsResponse filteredResponse = new UserAccountsResponse();
            filteredResponse.setAccounts(filteredAccounts);

            return filteredResponse;
        }

        return new UserAccountsResponse();
    }

    private void savePayment(PaymentsJoinRequest paymentsJoinRequest, User user) {
        paymentRepository.save(Payment.builder()
                .user(user)
                .creditLimit(paymentsJoinRequest.getCreditLimit())
                .repaymentDate(paymentsJoinRequest.getRepaymentDate())
                .password(paymentsJoinRequest.getPassword())
                .repaymentAccountNumber(paymentsJoinRequest.getRepaymentAccountNumber())
                .build());
    }

    private void saveMortgagedStocks(List<MortgagedStockRequest> morgagedStocksRequests, User user) {
        List<MortgagedStock> mortgagedStocks = new ArrayList<>();
        for (MortgagedStockRequest mortgagedStockRequest : morgagedStocksRequests) {
            mortgagedStocks.add(MortgagedStock.builder()
                    .accountNumber(mortgagedStockRequest.getAccountNumber())
                    .quantity(mortgagedStockRequest.getQuantity())
                    .stockCode(mortgagedStockRequest.getStockCode())
                    .companyCode(mortgagedStockRequest.getCompanyCode())
                    .user(user)
                    .build());
        }

        mortgagedStockRepository.saveAll(mortgagedStocks);
    }

    private void savePriorityStocks(List<StockPriorityRequest> stockPriorityRequests, User user) {
        List<StockPriority> stockPriorities = new ArrayList<>();
        for (StockPriorityRequest stockPriorityRequest : stockPriorityRequests) {
            stockPriorities.add(StockPriority.builder()
                    .accountNumber(stockPriorityRequest.getAccountNumber())
                    .stockRank(stockPriorityRequest.getStockRank())
                    .quantity(stockPriorityRequest.getQuantity())
                    .stockCode(stockPriorityRequest.getStockCode())
                    .companyCode(stockPriorityRequest.getCompanyCode())
                    .user(user)
                    .build());
        }

        stockPriorityRepository.saveAll(stockPriorities);
    }
}
