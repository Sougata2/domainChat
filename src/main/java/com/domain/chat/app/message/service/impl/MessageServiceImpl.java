package com.domain.chat.app.message.service.impl;

import com.domain.chat.app.message.dto.MessageDto;
import com.domain.chat.app.message.entity.MessageEntity;
import com.domain.chat.app.message.repository.MessageRepository;
import com.domain.chat.app.message.service.MessageService;
import com.domain.mapper.service.MapperService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {
    private final MessageRepository repository;
    private final MapperService mapper;

    @Override
    public List<MessageDto> findAll() {
        try {
            List<MessageEntity> entities = repository.findAll();
            return entities.stream().map(e -> (MessageDto) mapper.toDto(e)).toList();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public MessageDto findById(Long id) {
        try {
            Optional<MessageEntity> entity = repository.findById(id);
            if (entity.isEmpty()) {
                throw new EntityNotFoundException("Message with id %d not found".formatted(id));
            }
            return (MessageDto) mapper.toDto(entity.get());
        } catch (EntityNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    @Transactional
    public MessageDto create(MessageDto dto) {
        try {
            MessageEntity entity = (MessageEntity) mapper.toEntity(dto);
            MessageEntity saved = repository.save(entity);
            return (MessageDto) mapper.toDto(saved);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    @Transactional
    public MessageDto update(MessageDto dto) {
        try {
            Optional<MessageEntity> og = repository.findById(dto.getId());
            if (og.isEmpty()) {
                throw new EntityNotFoundException("Message with id %d not found".formatted(dto.getId()));
            }

            MessageEntity nu = (MessageEntity) mapper.toEntity(dto);
            MessageEntity merged = (MessageEntity) mapper.merge(og.get(), nu);
            MessageEntity saved = repository.save(merged);
            return (MessageDto) mapper.toDto(saved);
        } catch (EntityNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    @Transactional
    public MessageDto delete(MessageDto dto) {
        try {
            Optional<MessageEntity> entity = repository.findById(dto.getId());
            if (entity.isEmpty()) {
                throw new EntityNotFoundException("Message with id %d not found".formatted(dto.getId()));
            }

            repository.delete(entity.get());
            return dto;
        } catch (EntityNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
