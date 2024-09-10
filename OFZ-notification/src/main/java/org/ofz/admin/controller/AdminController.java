package org.ofz.admin.controller;

import lombok.RequiredArgsConstructor;
import org.ofz.admin.service.*;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequiredArgsConstructor
@RequestMapping("/notification")
public class AdminController {

    private final SimplePaymentSseService simplePaymentSseService;
    private final RepaymentSseService repaymentSseService;
    private final MarginRequirementSseService marginRequirementSseService;

    @GetMapping(value = "/payment", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter simplePaymentLog() {

        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
        simplePaymentSseService.addEmitter(emitter);

        emitter.onCompletion(simplePaymentSseService::removeEmitter);
        emitter.onTimeout(simplePaymentSseService::removeEmitter);

        return emitter;
    }

    @GetMapping(value = "/repayment", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter prepaymentLog() {

        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
        repaymentSseService.addEmitter(emitter);

        emitter.onCompletion(repaymentSseService::removeEmitter);
        emitter.onTimeout(repaymentSseService::removeEmitter);

        return emitter;
    }

    @GetMapping(value = "/margin-requirement", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter marginRequirementLog() {
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
        marginRequirementSseService.addEmitter(emitter);

        emitter.onCompletion(marginRequirementSseService::removeEmitter);
        emitter.onTimeout(marginRequirementSseService::removeEmitter);

        return emitter;
    }
}
