package org.ofz.offset.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class CurrentStockPriceReq {
    private List<String> stockCodes;
}
