package org.ofz.repayment.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.ofz.management.exception.FetchPreviousStockPriceException;
import org.ofz.repayment.exception.webclient.FetchRequestSellStockException;
import org.ofz.repayment.dto.PresentStockPriceDTO;
import org.ofz.repayment.dto.SellStockDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class StockUtils {

    @Value("${webclient.base-url}")
    private String PARTNERS_URL;
    private final WebClient webClient;

    public StockUtils(WebClient webClient) {
        this.webClient = webClient;
    }

    public PresentStockPriceDTO fetchPresentStockPrice(String stockCode) {

        PresentStockPriceDTO response = webClient.get()
                .uri("http://" + PARTNERS_URL + "/securities/stocks/" + stockCode + "/current")
                .retrieve()
                .bodyToMono(PresentStockPriceDTO.class).block();

        return response;
    }

    public int fetchPreviousStockPrice(String stockCode) {

        PreviousPriceDTO response;

        try {
            response = webClient.get()
                    .uri("http://" + PARTNERS_URL + "/securities/stocks/" + stockCode)
                    .retrieve()
                    .bodyToMono(PreviousPriceDTO.class).block();
        } catch (Exception e) {

            throw new FetchPreviousStockPriceException("전일 종가 가져오기 에러 " + e.getMessage());
        }

        if (response == null) {
            throw new FetchPreviousStockPriceException("받아온 전일 종가 데이터가 없습니다.");
        }

        return response.getAmount();
    }

    public SellAmountDTO fetchRequestSellStocks(SellStockDTO sellStock) {

        SellAmountDTO sellAmount;

        try {
             sellAmount = webClient.put()
                    .uri("http://" + PARTNERS_URL + "/securities/accounts/stocks")
                    .bodyValue(sellStock)
                    .retrieve()
                    .bodyToMono(SellAmountDTO.class).block();

        } catch (Exception e) {
            throw new FetchRequestSellStockException("증권 매도 요청에 실패했습니다. " + e.getMessage());
        }

        if (sellAmount == null) {
            throw new FetchRequestSellStockException("증권 매도 요청에 실패했습니다.");
        }

        return sellAmount;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SellAmountDTO {
        
        private int sellAmount;
    }
    
    @Getter
    public static class PreviousPriceDTO {

        private String stockCode;
        private int amount;

        public PreviousPriceDTO() {}

        public PreviousPriceDTO(String stockCode, int amount) {
            this.stockCode = stockCode;
            this.amount = amount;
        }
    }
}
