package com.domain.chat.app.room.service;

import com.domain.chat.app.message.dto.MessageDto;
import com.domain.chat.app.room.dto.RoomDto;
import com.domain.chat.app.room.dto.RoomListDto;
import com.domain.chat.app.room.dto.RoomOptDto;
import com.domain.chat.app.room.dto.RoomSummaryDto;

import java.util.List;
import java.util.UUID;

public interface RoomService {
    List<RoomDto> findAll();

    RoomDto findById(Long id);

    RoomDto findByReferenceNumber(UUID referenceNumber);

    RoomDto create(RoomDto dto);

    RoomDto update(RoomDto dto);

    RoomDto delete(RoomDto dto);

    List<MessageDto> getMessages(UUID referenceNumber);

    RoomListDto getSubscribedRooms();

    List<RoomSummaryDto> getSubscribedRoomsSummary();

    RoomOptDto getRoomOpt(UUID referenceNumber);

    RoomOptDto findRoomOpt(String participantEmail);

    MessageDto createPrivateRoom(RoomDto dto);

    RoomDto createGroupRoom(RoomDto dto);
}
