package org.ofz.management.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class SecuritiesResponse {
    private List<PreviousPrice> previousPricesDTO;
}
