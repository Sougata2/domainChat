package com.domain.chat.app.message.listener;

import com.domain.chat.app.message.entity.MessageEntity;
import com.domain.chat.app.pushNotification.service.PushNotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class MessageEventHandler {
    private final PushNotificationService pushNotificationService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void afterCommit(MessageEvent event) {
        MessageEntity message = event.entity();
        message.getRoom().getParticipants().forEach(participant -> {
            if (!participant.getId().equals(message.getSender().getId())) {
                if (message.getRoom().getRoomType().equals("GROUP")) {
                    String messageBody = "%s : %s".formatted(message.getSender().getFirstName(), message.getMessage());
                    pushNotificationService.notifyUser(
                            participant,
                            message.getRoom().getGroupName(),
                            messageBody
                    );
                } else if (message.getRoom().getRoomType().equals("PRIVATE")) {
                    pushNotificationService.notifyUser(
                            participant,
                            message.getSender().getFirstName() + " " + message.getSender().getLastName(),
                            message.getMessage()
                    );
                }
            }
        });
    }
}
