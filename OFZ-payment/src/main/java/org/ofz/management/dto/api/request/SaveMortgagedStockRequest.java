package org.ofz.management.dto.api.request;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.ofz.management.dto.common.MortgagedStockDto;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class SaveMortgagedStockRequest {
    private final String loginId;
    private final List<MortgagedStockDto> mortgagedStocks;
}
