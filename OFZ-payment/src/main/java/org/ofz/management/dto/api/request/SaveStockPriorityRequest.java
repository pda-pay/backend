package org.ofz.management.dto.api.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.ofz.management.dto.common.StockPriorityDto;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class SaveStockPriorityRequest {
    private String loginId;
    private List<StockPriorityDto> stockPriorities;

    public void setLoginId(String loginId) {
        this.loginId = loginId;
    }
}
