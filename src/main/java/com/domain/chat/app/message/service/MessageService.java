package com.domain.chat.app.message.service;

import com.domain.chat.app.message.dto.MessageDto;
import com.domain.chat.app.message.entity.MessageEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface MessageService {
    List<MessageDto> findAll();

    MessageDto findById(Long id);

    MessageDto create(MessageDto dto);

    MessageDto update(MessageDto dto);

    MessageDto delete(MessageDto dto);

    MessageDto send(MessageDto dto);

    MessageDto send(MessageDto dto, String eventType);

    MessageEntity sendAndReturnEntity(MessageDto dto, String eventType);

    MessageDto sendFile(MessageDto dto, MultipartFile file);
}
