package org.ofz.smsAuth;

import net.nurigo.sdk.message.model.Message;
import net.nurigo.sdk.message.service.DefaultMessageService;
import net.nurigo.sdk.message.request.SingleMessageSendingRequest;

public class SmsUtil {

    public static Message createMessage(String from, String to, String text) {
        Message message = new Message();
        message.setFrom(from);
        message.setTo(to);
        message.setText(text);
        return message;
    }

    public static void sendMessage(DefaultMessageService messageService, Message message) {
        messageService.sendOne(new SingleMessageSendingRequest(message));
    }
}
