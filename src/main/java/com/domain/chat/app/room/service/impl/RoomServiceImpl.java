package com.domain.chat.app.room.service.impl;

import com.domain.chat.app.message.dto.MessageDto;
import com.domain.chat.app.message.entity.MessageEntity;
import com.domain.chat.app.message.repository.MessageRepository;
import com.domain.chat.app.room.dto.RoomDto;
import com.domain.chat.app.room.dto.RoomListDto;
import com.domain.chat.app.room.dto.RoomOptDto;
import com.domain.chat.app.room.dto.RoomSummaryDto;
import com.domain.chat.app.room.entity.RoomEntity;
import com.domain.chat.app.room.repository.RoomRepository;
import com.domain.chat.app.room.service.RoomService;
import com.domain.chat.app.user.dto.UserDto;
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

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

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
    public RoomDto findByReferenceNumber(UUID referenceNumber) {
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
    public List<MessageDto> getMessages(UUID referenceNumber) {
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
    public RoomListDto getSubscribedRooms() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<UserEntity> user = userRepository.findByEmail(username);
        if (user.isEmpty()) {
            throw new EntityNotFoundException("User not found with email %s not found".formatted(username));
        }
        List<RoomEntity> entities = repository.findByUserIdWithLatestMessage(user.get().getId());
        Map<UUID, RoomDto> roomMap = entities.stream().map(e -> (RoomDto) mapper.toDto(e)).collect(Collectors.toMap(RoomDto::getReferenceNumber, Function.identity()));
        List<UUID> references = repository.getSubscribedRoomsReference(user.get().getId());
        return RoomListDto.builder()
                .rooms(roomMap)
                .references(references)
                .build();
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
                    r.roomType,
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
    public RoomOptDto getRoomOpt(UUID referenceNumber) {
        try {
            RoomEntity room = repository.findByReferenceNumber(referenceNumber).orElseThrow(() -> new EntityNotFoundException("Room with reference number %s not found".formatted(referenceNumber)));
            return convertRoomToRoomOpt(room);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public RoomOptDto findRoomOpt(String participantEmail) {
        String loggedInUserEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity loggedInUser = userRepository.findByEmail(loggedInUserEmail).orElseThrow(() -> new EntityNotFoundException("User with email %s is not found".formatted(loggedInUserEmail)));
        UserEntity participant = userRepository.findByEmail(participantEmail).orElseThrow(() -> new EntityNotFoundException("User with email %s not found".formatted(participantEmail)));

        Set<Long> participants = Set.of(loggedInUser.getId(), participant.getId());
        RoomEntity room = repository.findRoomOpt(participants, "PRIVATE").orElseThrow(() -> new EntityNotFoundException("PRIVATE Room with participants id: %d, %d is not found".formatted(loggedInUser.getId(), participant.getId())));
        return convertRoomToRoomOpt(room);
    }

    @Override
    @Transactional
    public MessageDto createPrivateRoom(RoomDto dto) {
        Set<MessageDto> messagesDto = dto.getMessages();
        Set<UserDto> participantsDto = dto.getParticipants();
        dto.setMessages(null);
        dto.setParticipants(null);
        RoomEntity newRoom = (RoomEntity) mapper.toEntity(dto);
        Set<UserEntity> participants = new LinkedHashSet<>();
        for (UserDto user : participantsDto) {
            UserEntity participant = userRepository.findByEmail(user.getEmail()).orElseThrow(() -> new EntityNotFoundException("User with email %s not found".formatted(user.getEmail())));
            participants.add(participant);
        }
        newRoom.setParticipants(participants);
        newRoom.setRoomType("PRIVATE");

        RoomEntity saved = repository.save(newRoom);
        MessageDto messageDto = messagesDto.stream().toList().getFirst();
        messageDto.getRoom().setReferenceNumber(saved.getReferenceNumber());
        return messageDto;
    }

    @Override
    public RoomDto createGroupRoom(RoomDto dto) {
        RoomEntity newGroupRoom = (RoomEntity) mapper.toEntity(dto);
        Set<UserEntity> participants = new LinkedHashSet<>();
        for (UserDto user : dto.getParticipants()) {
            UserEntity u = userRepository.findByEmail(user.getEmail()).orElseThrow(() -> new EntityNotFoundException("User with email %s not found".formatted(user.getEmail())));
            participants.add(u);
        }
        newGroupRoom.setParticipants(participants);
        newGroupRoom.setRoomType("GROUP");
        RoomEntity saved = repository.save(newGroupRoom);

        RoomDto roomDto = (RoomDto) mapper.toDto(saved);
        for (UserEntity participant : participants) {
            emitterRegistry.broadcast(participant.getEmail(), "ROOM", roomDto);
        }
        return roomDto;
    }

    @Override
    @Transactional
    public RoomDto muteUser(UUID roomReferenceNumber) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        RoomEntity roomEntity = repository.findByReferenceNumber(roomReferenceNumber)
                .orElseThrow(() -> new EntityNotFoundException("Room %s is not found".formatted(roomReferenceNumber)));

        UserEntity userEntity = userRepository.findByEmail(username)
                .orElseThrow(() -> new EntityNotFoundException("User %s is not found".formatted(username)));

        roomEntity.getMutedParticipants().add(userEntity);
        RoomEntity saved = repository.save(roomEntity);
        return (RoomDto) mapper.toDto(saved);
    }

    @Override
    @Transactional
    public RoomDto unMuteUser(UUID roomReferenceNumber) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        RoomEntity roomEntity = repository.findByReferenceNumber(roomReferenceNumber)
                .orElseThrow(() -> new EntityNotFoundException("Room %s is not found".formatted(roomReferenceNumber)));

        UserEntity userEntity = userRepository.findByEmail(username)
                .orElseThrow(() -> new EntityNotFoundException("User %s is not found".formatted(username)));
        roomEntity.getMutedParticipants().remove(userEntity);
        RoomEntity saved = repository.save(roomEntity);
        return (RoomDto) mapper.toDto(saved);
    }

    private RoomOptDto convertRoomToRoomOpt(RoomEntity room) {
        List<MessageEntity> messageEntities = messageRepository.findByRoomReferenceNumber(room.getReferenceNumber());
        Map<String, MessageDto> messageDtos = messageEntities
                .stream()
                .map(e -> (MessageDto) mapper.toDto(e))
                .collect(Collectors.toMap(MessageDto::getUuid, Function.identity()));
        List<String> uuids = messageRepository.findMessageUUIDsByRoomReference(room.getReferenceNumber());
        List<UserDto> participants = room.getParticipants().stream().map(e -> (UserDto) mapper.toDto(e)).toList();
        List<String> mutedParticipants = room.getMutedParticipants().stream().map(UserEntity::getEmail).toList();
        return RoomOptDto.builder()
                .id(room.getId())
                .referenceNumber(room.getReferenceNumber())
                .groupName(room.getGroupName())
                .roomType(room.getRoomType())
                .participants(participants)
                .uuids(uuids)
                .messages(messageDtos)
                .mutedParticipants(mutedParticipants)
                .createdAt(room.getCreatedAt())
                .updatedAt(room.getUpdatedAt())
                .build();
    }
}
