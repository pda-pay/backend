package org.ofz.offset.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SellStockReq {
    private String accountNumber;
    private int quantity;
    private String stockCode;
}
