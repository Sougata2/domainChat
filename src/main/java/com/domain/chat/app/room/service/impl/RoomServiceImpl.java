package com.domain.chat.app.room.service.impl;

import com.domain.chat.app.message.dto.MessageDto;
import com.domain.chat.app.message.entity.MessageEntity;
import com.domain.chat.app.message.repository.MessageRepository;
import com.domain.chat.app.room.dto.RoomDto;
import com.domain.chat.app.room.dto.RoomSummaryDto;
import com.domain.chat.app.room.entity.RoomEntity;
import com.domain.chat.app.room.repository.RoomRepository;
import com.domain.chat.app.room.service.RoomService;
import com.domain.chat.app.user.entity.UserEntity;
import com.domain.chat.app.user.repository.UserRepository;
import com.domain.chat.component.emitter.EmitterRegistry;
import com.domain.mapper.service.MapperService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RoomServiceImpl implements RoomService {
    private final MessageRepository messageRepository;
    private final EmitterRegistry emitterRegistry;
    private final UserRepository userRepository;
    private final RoomRepository repository;
    private final MapperService mapper;
    private final EntityManager em;

    @Override
    public List<RoomDto> findAll() {
        try {
            List<RoomEntity> entities = repository.findAll();
            return entities.stream().map(e -> (RoomDto) mapper.toDto(e)).toList();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public RoomDto findById(Long id) {
        try {
            Optional<RoomEntity> entity = repository.findById(id);
            if (entity.isEmpty()) {
                throw new EntityNotFoundException("Room not found with id %d not found".formatted(id));
            }
            return (RoomDto) mapper.toDto(entity.get());
        } catch (EntityNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public RoomDto findByReferenceNumber(String referenceNumber) {
        try {
            Optional<RoomEntity> entity = repository.findByReferenceNumber(referenceNumber);
            if (entity.isEmpty()) {
                throw new EntityNotFoundException("Room not found with reference number %s not found".formatted(referenceNumber));
            }
            return (RoomDto) mapper.toDto(entity.get());
        } catch (EntityNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    @Transactional
    public RoomDto create(RoomDto dto) {
        try {
            RoomEntity entity = (RoomEntity) mapper.toEntity(dto);
            RoomEntity saved = repository.save(entity);
            return (RoomDto) mapper.toDto(saved);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    @Transactional
    public RoomDto update(RoomDto dto) {
        try {
            Optional<RoomEntity> og = repository.findByReferenceNumber(dto.getReferenceNumber());
            if (og.isEmpty()) {
                throw new EntityNotFoundException("Room not found with reference number %s not found".formatted(dto.getReferenceNumber()));
            }
            RoomEntity nu = (RoomEntity) mapper.toEntity(dto);
            RoomEntity merged = (RoomEntity) mapper.merge(og.get(), nu);
            RoomEntity saved = repository.save(merged);
            return (RoomDto) mapper.toDto(saved);
        } catch (EntityNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    @Transactional
    public RoomDto delete(RoomDto dto) {
        try {
            Optional<RoomEntity> entity = repository.findByReferenceNumber(dto.getReferenceNumber());
            if (entity.isEmpty()) {
                throw new EntityNotFoundException("Room not found with reference number %s not found".formatted(dto.getReferenceNumber()));
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
    public List<MessageDto> getMessages(String referenceNumber) {
        try {
            Optional<RoomEntity> room = repository.findByReferenceNumber(referenceNumber);
            if (room.isEmpty()) {
                throw new EntityNotFoundException("Room not found with reference number %s not found".formatted(referenceNumber));
            }

            String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
            boolean isParticipant = room.get().getParticipants().stream().anyMatch(u -> u.getEmail().equals(userEmail));
            if (!isParticipant) {
                throw new AuthorizationDeniedException("%s is not participant of this room".formatted(userEmail));
            }

            List<MessageEntity> messages = messageRepository.findByRoomReferenceNumberDesc(referenceNumber);
            return messages.stream().map(e -> (MessageDto) mapper.toDto(e)).toList();
        } catch (EntityNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<RoomDto> getSubscribedRooms() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<UserEntity> user = userRepository.findByEmail(username);
        if (user.isEmpty()) {
            throw new EntityNotFoundException("User not found with email %s not found".formatted(username));
        }
        List<RoomEntity> entities = repository.findByUserIdWithLatestMessage(user.get().getId());
        return entities.stream().map(e -> (RoomDto) mapper.toDto(e)).toList();
    }

    @Override
    public List<RoomSummaryDto> getSubscribedRoomsSummary() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<UserEntity> user = userRepository.findByEmail(username);
        if (user.isEmpty()) {
            throw new EntityNotFoundException("User not found with email %s not found".formatted(username));
        }
        TypedQuery<RoomSummaryDto> query = em.createQuery("""
                select new com.domain.chat.app.room.dto.RoomSummaryDto(
                    r.id,
                    r.referenceNumber,
                    m.message,
                    m.sender.email,
                    op.firstName,
                    op.lastName,
                    m.createdAt,
                    m.updatedAt
                )
                from RoomEntity r
                join r.participants p
                join r.participants op
                left join r.messages m
                where p.id = :userId
                and op.id <> :userId
                and (
                    m.id is null or m.createdAt = (
                        select max(m2.createdAt)
                        from MessageEntity m2
                        where m2.room = r
                    )
                )
                order by m.createdAt desc
                """, RoomSummaryDto.class);
        query.setParameter("userId", user.get().getId());
        return query.getResultList();
    }

    @Override
    @Deprecated
    public SseEmitter streamRoom(String referenceNumber) {
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<UserEntity> user = userRepository.findByEmail(userEmail);
        if (user.isEmpty()) {
            throw new EntityNotFoundException("User with email %s not found".formatted(userEmail));
        }

        Optional<RoomEntity> room = repository.findByReferenceNumber(referenceNumber);
        if (room.isEmpty()) {
            throw new EntityNotFoundException("Room with reference number %s not found".formatted(referenceNumber));
        }

        boolean isParticipant = room.get().getParticipants().stream().anyMatch(u -> Objects.equals(u.getId(), user.get().getId()));
        if (!isParticipant) {
            throw new AuthorizationDeniedException("%s is not participant of this room".formatted(userEmail));
        }

        SseEmitter emitter = emitterRegistry.addEmitter(referenceNumber);
        try {
            List<MessageDto> oldMessages = messageRepository.findByRoomReferenceNumber(referenceNumber)
                    .stream()
                    .map(e -> (MessageDto) mapper.toDto(e)).toList();
            emitter.send(SseEmitter.event().name("history").data(oldMessages));
        } catch (Exception e) {
            emitter.completeWithError(e);
            throw new RuntimeException(e);
        }
        return emitter;
    }
}
