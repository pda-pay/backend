package org.ofz.rabbitMQ.rabbitDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.ofz.rabbitMQ.Queueable;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MarginRequirementLogDto implements Queueable {

    private Long userId;
    private String loginId;
    private String title;
    private String message;
    private int mortgageSum;
    private int creditLimit;
    private int marginRequirement;
    private int maxLimit;
    private boolean isAboveThreshold; // 140% 이하인지 여부

    @Override
    public String getQueueName() {
        return "marginRequirementLog";
    }
}
