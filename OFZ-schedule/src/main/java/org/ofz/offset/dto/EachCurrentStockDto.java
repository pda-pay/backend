package org.ofz.offset.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class EachCurrentStockDto {
    private String stockCode;
    private int amount;
}
