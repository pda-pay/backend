package org.ofz.cacheStockPrice.controller;

import lombok.RequiredArgsConstructor;
import org.ofz.cacheStockPrice.dto.StockPriceProcessResponse;
import org.ofz.cacheStockPrice.dto.TriggerRequest;
import org.ofz.cacheStockPrice.service.StockPriceScheduleService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/stock-price")
public class StockPriceController {
    @Value("${authentication.signature}")
    private String authentication;
    final private StockPriceScheduleService stockPriceScheduleService;
    @PostMapping("/trigger")
    public ResponseEntity<StockPriceProcessResponse> triggerCacheStockPrice(@RequestBody TriggerRequest triggerRequest) {
        if (triggerRequest.getPassword().equals(authentication)) {
            stockPriceScheduleService.cachePreviousStockPrice();
            return ResponseEntity.status(HttpStatus.OK).body(StockPriceProcessResponse.success());

        } else {
            return ResponseEntity.status(HttpStatus.OK).body(StockPriceProcessResponse.fail());
        }
    }
}
