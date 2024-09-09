package org.ofz.rabbitMQ.rabbitDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.ofz.rabbitMQ.Queueable;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AllPayedOffsetLogDto implements Queueable {
    // 해당 사용자 stock, mortgaged_stock, stock_priority 변경
    // if(excessPayment > 0)해당 사용자 'mortgagedStockProjection.getAccountNumber()'의 잔고 excessPayment만큼 증가
    // 해당 사용자의 payment 테이블 previous_month_debt 0으로, overdueDay null로, payFlag true으로 변경됨

    private Long id;
    private String loginId;
    private String title;
    private String contentsAboutStock;
    private String contentsAboutAccount;
    private String contentsAboutPayment;

    @Override
    public String getQueueName() {
        return "adminOffset";
    }
}
