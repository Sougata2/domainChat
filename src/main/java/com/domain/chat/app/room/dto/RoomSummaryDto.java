package com.domain.chat.app.room.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RoomSummaryDto {
    private Long id;
    private String referenceNumber;
    private String latestMessage;
    private String latestMessageSenderEmail;
    private String otherParticipantFirstName;
    private String otherParticipantLastName;
    private LocalDateTime latestMessageSentAt;
    private LocalDateTime latestMessageUpdatedAt;
}
