package org.ofz.asset;

import org.ofz.rabbitMQ.rabbitDto.AssetMqDTO;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AssetHistoryConsumer {

    @Autowired
    private AssetHistoryService assetHistoryService;

    @RabbitListener(queues = "asset")
    public void receiveMessage(AssetMqDTO assetMqDTO) {
        assetHistoryService.saveAssetHistoryFromMq(assetMqDTO);
    }
}