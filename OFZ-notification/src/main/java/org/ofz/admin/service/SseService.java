package org.ofz.admin.service;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public interface SseService<T> {

    void addEmitter(SseEmitter emitter);
    void removeEmitter();
    void sendLogEvent(T t);
}
