package com.domain.chat.app.message.listener;

import com.domain.chat.app.message.dto.MessageDto;
import com.domain.chat.app.message.entity.MessageEntity;
import com.domain.chat.app.pushNotification.service.PushNotificationService;
import com.domain.chat.app.room.entity.RoomEntity;
import com.domain.chat.app.user.dto.UserDto;
import com.domain.chat.app.user.entity.UserEntity;
import com.domain.chat.component.emitter.EmitterRegistry;
import com.domain.mapper.service.MapperService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.LinkedHashSet;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class MessageEventHandler {
    private final PushNotificationService pushNotificationService;
    private final EmitterRegistry emitterRegistry;
    private final MapperService mapper;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void afterCommit(MessageEvent event) {
        MessageEntity message = event.entity();
        UserEntity sender = message.getSender();
        RoomEntity room = message.getRoom();

        MessageDto outGoing = (MessageDto) mapper.toDto(message);
        if (outGoing.getSenderFirstName() == null) outGoing.setSenderFirstName(sender.getFirstName());
        if (outGoing.getSenderLastName() == null) outGoing.setSenderLastName(sender.getLastName());
        Set<UserDto> participants = new LinkedHashSet<>();
        for (UserEntity participant : room.getParticipants()) {
            UserDto p = new UserDto();
            p.setId(participant.getId());
            p.setEmail(participant.getEmail());
            p.setFirstName(participant.getFirstName());
            p.setLastName(participant.getLastName());
            participants.add(p);
        }

        outGoing.getRoom().setParticipants(participants);
        room.getParticipants().forEach(participant -> {
            emitterRegistry.broadcast(participant.getEmail(), message.getEventType(), outGoing);
        });

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
