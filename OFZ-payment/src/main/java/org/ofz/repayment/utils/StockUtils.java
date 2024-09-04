package org.ofz.repayment.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.ofz.repayment.dto.PresentStockPriceDTO;
import org.ofz.repayment.dto.SellStockDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.HashMap;
import java.util.Map;

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

        PreviousPriceDTO response = webClient.get()
                .uri("http://" + PARTNERS_URL + "/securities/stocks/" + stockCode)
                .retrieve()
                .bodyToMono(PreviousPriceDTO.class).block();

        if (response == null) {
            throw new RuntimeException("널이다 씨잇팔~~");
        }

        return response.getAmount();
    }

    public SellAmountDTO fetchRequestSellStocks(SellStockDTO sellStock) {

        SellAmountDTO sellAmount = webClient.put()
                .uri("http://" + PARTNERS_URL + "/securities/accounts/stocks")
                .bodyValue(sellStock)
                .retrieve()
                .bodyToMono(SellAmountDTO.class).block();

        if (sellAmount == null) {
            throw new RuntimeException("널널널");
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
