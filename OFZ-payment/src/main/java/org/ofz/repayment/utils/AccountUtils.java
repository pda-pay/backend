package org.ofz.repayment.utils;

import org.ofz.repayment.dto.response.CashRepaymentResponse;
import org.ofz.repayment.dto.response.AccountResponse;
import org.ofz.repayment.exception.webclient.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.HashMap;
import java.util.Map;

@Service
public class AccountUtils {

    @Value("${partner.api.signup-stock-data}")
    private String PARTNERS_URL;
    private final WebClient webClient;

    public AccountUtils(WebClient webClient) {
        this.webClient = webClient;
    }

    public synchronized AccountResponse fetchPaymentAccount(Long accountId) {

        System.out.println("PARTNERS_URL = " + PARTNERS_URL);

        AccountResponse response = null;

        Map<String, String> map = new HashMap<>();
        map.put("accountNumber", "381-6598-15037");

        try {

            response = webClient.post()
                    .uri(PARTNERS_URL + "/accounts/deposits")
                    .bodyValue(map)
                    .retrieve()
                    .bodyToMono(AccountResponse.class).block();

            if (response == null) {
                throw new WebClientResponseNullPointerException("응답 받은 상환 계좌 정보 데이터가 없습니다.");
            }

        } catch (WebClientRequestException e) {
            throw new WebClientBadRequestException("네트워크 연결 문제 또는 잘못된 URI 요청입니다.");

        } catch (WebClientResponseException  e) {
            if (e.getStatusCode().is4xxClientError()) {
                throw new WebClientErrorException("요청 데이터를 확인해주세요.", e.getStatusCode());

            } else if (e.getStatusCode().is5xxServerError()) {
                throw new WebClientServerErrorException("서버 에러가 발생했습니다.", e.getStatusCode());
            }

        } catch (IllegalArgumentException e) {
            throw new WebClientIllegalArgumentException("잘못된 인자를 메서드에 전달하였습니다.", e.getMessage());
        }

        response.setAccountNumber("어카운트 넘버");

        return response;
    }

    public synchronized CashRepaymentResponse fetchCashRepayment(Long accountId, int value) {
        CashRepaymentResponse response = null;

        Map<String, Object> map = new HashMap<>();
        map.put("accountNumber", "381-6598-15037");
        map.put("value", value);

        try {

            response = webClient.put()
                    .uri(PARTNERS_URL + "/accounts/withdraw")
                    .bodyValue(map)
                    .retrieve()
                    .bodyToMono(CashRepaymentResponse.class).block();

            if (response == null) {
                throw new WebClientResponseNullPointerException("응답 받은 상환 계좌 정보 데이터가 없습니다.");
            }

        } catch (WebClientRequestException e) {
            throw new WebClientBadRequestException("네트워크 연결 문제 또는 잘못된 URI 요청입니다.");

        } catch (WebClientResponseException  e) {
            if (e.getStatusCode().is4xxClientError()) {
                throw new WebClientErrorException("요청 데이터를 확인해주세요.", e.getStatusCode());

            } else if (e.getStatusCode().is5xxServerError()) {
                throw new WebClientServerErrorException("서버 에러가 발생했습니다.", e.getStatusCode());
            }

        } catch (IllegalArgumentException e) {
            throw new WebClientIllegalArgumentException("잘못된 인자를 메서드에 전달하였습니다.", e.getMessage());
        }

        response.setRepaymentAmount(value);
        return response;
    }
}
