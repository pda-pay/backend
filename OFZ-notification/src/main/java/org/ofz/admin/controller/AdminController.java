package org.ofz.admin.controller;

import lombok.RequiredArgsConstructor;
import org.ofz.admin.service.AllPayedOffsetSseService;
import org.ofz.admin.service.NotAllPayedOffsetSseService;
import org.ofz.admin.service.RepaymentSseService;
import org.ofz.admin.service.SimplePaymentSseService;
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
    private final AllPayedOffsetSseService allPayedOffsetSseService;
    private final NotAllPayedOffsetSseService notAllPayedOffsetSseService;

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

    @GetMapping(value = "/offset/all-payed", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter allPayedOffsetLog() {

        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
        allPayedOffsetSseService.addEmitter(emitter);

        emitter.onCompletion(allPayedOffsetSseService::removeEmitter);
        emitter.onTimeout(allPayedOffsetSseService::removeEmitter);

        return emitter;
    }

    @GetMapping(value = "/offset/not-all-payed", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter notAllPayedOffsetLog() {

        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
        notAllPayedOffsetSseService.addEmitter(emitter);

        emitter.onCompletion(notAllPayedOffsetSseService::removeEmitter);
        emitter.onTimeout(notAllPayedOffsetSseService::removeEmitter);

        return emitter;
    }
}
