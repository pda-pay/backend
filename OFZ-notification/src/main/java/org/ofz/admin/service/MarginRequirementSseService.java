//// MarginRequirementSseService.java
//package org.ofz.admin.service;
//
//import lombok.RequiredArgsConstructor;
//import org.ofz.rabbitMQ.rabbitDto.MarginRequirementLogDto;
//import org.springframework.amqp.rabbit.annotation.RabbitListener;
//import org.springframework.stereotype.Service;
//import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
//
//import java.io.IOException;
//
//@Service
//@RequiredArgsConstructor
//public class MarginRequirementSseService implements SseService<MarginRequirementLogDto> {
//    private final static String MARGIN_REQUIREMENT_QUEUE = "marginRequirementQueue";
//    private SseEmitter marginRequirementEmitter;
//
//    @Override
//    public void addEmitter(SseEmitter emitter) {
//        this.marginRequirementEmitter = emitter;
//    }
//
//    @Override
//    public void removeEmitter() {
//        this.marginRequirementEmitter = null;
//    }
//
//    @Override
//    public void sendLogEvent(MarginRequirementLogDto log) {
//        if (marginRequirementEmitter != null) {
//            try {
//                marginRequirementEmitter.send(SseEmitter.event()
//                        .name(MARGIN_REQUIREMENT_QUEUE)
//                        .data(log));
//            } catch (IOException e) {
//                removeEmitter();
//                throw new RuntimeException("MarginRequirement SSE 에러: " + e.getMessage());
//            }
//        }
//    }
//
//    @RabbitListener(queues = MARGIN_REQUIREMENT_QUEUE)
//    public void receiveMessage(MarginRequirementLogDto log) {
//        try {
//            Thread.sleep(1000);
//            sendLogEvent(log);
//        } catch (Exception e) {
//            Thread.currentThread().interrupt();
//            throw new RuntimeException("MarginRequirement MQ 에러: " + e.getMessage());
//        }
//    }
//}
