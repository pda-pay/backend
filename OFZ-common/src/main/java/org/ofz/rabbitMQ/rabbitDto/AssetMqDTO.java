package org.ofz.rabbitMQ.rabbitDto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.ofz.rabbitMQ.Queueable;

import java.time.LocalDateTime;

import static org.ofz.rabbitMQ.utils.DateUtil.formatLocalDateTime;

@Getter
@NoArgsConstructor
public class AssetMqDTO implements Queueable {

    private Long userId;
    private int mortgageSum;
    private int todayLimit;
    private int maxLimit;
    private int margin_requirement;

    @Builder
    public AssetMqDTO(Long userId, int mortgageSum, int todayLimit, int maxLimit, int margin_requirement) {
        this.userId = userId;
        this.mortgageSum = mortgageSum;
        this.todayLimit = todayLimit;
        this.maxLimit = maxLimit;
        this.margin_requirement = margin_requirement;
    }

    @Override
    public String getQueueName() {
        return "asset";
    }
}
