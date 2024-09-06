package org.ofz.offset.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CurrentStockPriceRes {
    @JsonProperty("currentPricesDTO")
    private List<EachCurrentStockDto> currentStockList;
}
