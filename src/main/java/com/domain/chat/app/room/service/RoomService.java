package com.domain.chat.app.room.service;

import com.domain.chat.app.message.dto.MessageDto;
import com.domain.chat.app.room.dto.RoomDto;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

public interface RoomService {
    List<RoomDto> findAll();

    RoomDto findById(Long id);

    RoomDto findByReferenceNumber(String referenceNumber);

    RoomDto create(RoomDto dto);

    RoomDto update(RoomDto dto);

    RoomDto delete(RoomDto dto);

    List<MessageDto> getMessages(String referenceNumber);

    List<RoomDto> getSubscribedRooms();

    @Deprecated
    SseEmitter streamRoom(String referenceNumber);
}
