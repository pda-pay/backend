package org.ofz.management.dto.api.request;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.ofz.management.dto.common.StockPriorityDto;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class SaveRepaymentDateRequest {
    private final String loginId;
    private final int repaymentDate;
}
