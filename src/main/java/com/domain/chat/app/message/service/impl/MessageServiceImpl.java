package com.domain.chat.app.message.service.impl;

import com.domain.chat.app.file.service.FileService;
import com.domain.chat.app.message.dto.MessageDto;
import com.domain.chat.app.message.entity.MessageEntity;
import com.domain.chat.app.message.repository.MessageRepository;
import com.domain.chat.app.message.service.MessageService;
import com.domain.chat.app.room.entity.RoomEntity;
import com.domain.chat.app.room.repository.RoomRepository;
import com.domain.chat.app.user.entity.UserEntity;
import com.domain.chat.app.user.repository.UserRepository;
import com.domain.mapper.service.MapperService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {
    //    private final EmitterRegistry emitterRegistry;
    private final UserRepository userRepository;
    private final RoomRepository roomRepository;
    private final MessageRepository repository;
    private final FileService fileService;
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

    @Override
    @Transactional
    public MessageDto send(MessageDto dto) {
        return send(dto, "MESSAGE");
    }

    @Override
    @Transactional
    public MessageDto send(MessageDto dto, String eventType) {
        try {
            MessageEntity prepared = prepareMessageEntity(dto, eventType);
            MessageEntity saved = repository.save(prepared);
            return (MessageDto) mapper.toDto(saved);
/*
            if (outGoing.getSenderFirstName() == null) outGoing.setSenderFirstName(sender.get().getFirstName());
            if (outGoing.getSenderLastName() == null) outGoing.setSenderLastName(sender.get().getLastName());
            Set<UserDto> participants = new LinkedHashSet<>();
            for (UserEntity participant : room.get().getParticipants()) {
                UserDto p = new UserDto();
                p.setId(participant.getId());
                p.setEmail(participant.getEmail());
                p.setFirstName(participant.getFirstName());
                p.setLastName(participant.getLastName());
                participants.add(p);
            }
            outGoing.getRoom().setParticipants(participants);
            room.get().getParticipants().forEach(participant -> {
                emitterRegistry.broadcast(participant.getEmail(), eventType, outGoing);
            });
            return outGoing;
*/
        } catch (EntityNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public MessageEntity prepareMessageEntity(MessageDto dto, String eventType) {
        try {
            String email = SecurityContextHolder.getContext().getAuthentication().getName();
            Optional<UserEntity> sender = userRepository.findByEmail(email);
            if (sender.isEmpty()) {
                throw new EntityNotFoundException("Sender with id %d not found".formatted(dto.getSender().getId()));
            }

            Optional<RoomEntity> room = roomRepository.findByReferenceNumber(dto.getRoom().getReferenceNumber());
            if (room.isEmpty()) {
                throw new EntityNotFoundException("Room with reference number %s not found".formatted(dto.getRoom().getReferenceNumber()));
            }

            MessageEntity message = (MessageEntity) mapper.toEntity(dto);
            message.setSender(sender.get());
            message.setRoom(room.get());
            message.setEventType(eventType);
            room.get().setLastMessageSentAt(LocalDateTime.now());
            roomRepository.save(room.get());
            return message;
        } catch (EntityNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    @Transactional
    public MessageDto sendFile(MessageDto dto, MultipartFile file) {
        MessageDto message = send(dto, "MEDIA");

        if (file != null) {
            MessageEntity messageEntity = repository.findById(message.getId())
                    .orElseThrow(
                            () -> new EntityNotFoundException("Message with id %d not found".formatted(message.getId()))
                    );
            fileService.upload(file, messageEntity);
        }

        MessageEntity finalMessage = repository.findById(message.getId())
                .orElseThrow(
                        () -> new EntityNotFoundException("Message with id %d not found".formatted(message.getId()))
                );
        return (MessageDto) mapper.toDto(finalMessage);
    }
}
