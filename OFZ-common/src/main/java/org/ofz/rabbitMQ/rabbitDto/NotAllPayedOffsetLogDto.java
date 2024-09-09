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
public class NotAllPayedOffsetLogDto implements Queueable {
    private Long id;
    private String loginId;
    private String title;
    private String contentsAboutStock;
    private String contentsAboutPayment;

    @Override
    public String getQueueName() {
        return "nofication";
    }
}
