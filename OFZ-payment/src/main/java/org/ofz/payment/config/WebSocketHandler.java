package org.ofz.payment.config;

import org.ofz.payment.exception.websocket.InvalidUriException;
import org.ofz.payment.exception.websocket.SocketIdNullException;
import org.ofz.payment.exception.websocket.WebSocketScheduleException;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Component
public class WebSocketHandler extends TextWebSocketHandler {

    private static final ConcurrentHashMap<String, WebSocketSession>
    CLIENTS = new ConcurrentHashMap<>();

    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(10);

    public WebSocketSession getSession(String id) {
        return CLIENTS.get(id);
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        validationUri(session.getUri());

        String uri = session.getUri().toString();
        String id = extractIdFromUri(uri);

        CLIENTS.put(id, session);

        scheduler.schedule(() -> {
            WebSocketSession removedSession = CLIENTS.remove(id);

            if (removedSession != null && removedSession.isOpen()) {

                try {
                    removedSession.close(CloseStatus.GOING_AWAY);
                } catch (IOException e) {
                    throw new WebSocketScheduleException("웹소켓 스케쥴링 에러: " + e.getMessage());
                }
            }
        }, 1, TimeUnit.MINUTES);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws IOException {
        validationUri(session.getUri());

        String uri = session.getUri().toString();
        String id = extractIdFromUri(uri);

        CLIENTS.remove(id);
    }

    private void validationUri(URI uri) {

        if (uri == null) {
            throw new InvalidUriException("유효한 URI가 아닙니다");
        }
    }

    private String extractIdFromUri(String uri) {

        String id = null;

        if (uri.contains("id=")) {
            id = URLDecoder.decode(uri.split("id=")[1], StandardCharsets.UTF_8);
        }

        if (id == null) {
            throw new SocketIdNullException("소켓 아이디가 없습니다.");
        }

        return id;
    }
}
