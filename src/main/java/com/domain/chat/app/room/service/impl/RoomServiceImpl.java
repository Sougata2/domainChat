package com.domain.chat.app.room.service.impl;

import com.domain.chat.app.room.dto.RoomDto;
import com.domain.chat.app.room.entity.RoomEntity;
import com.domain.chat.app.room.repository.RoomRepository;
import com.domain.chat.app.room.service.RoomService;
import com.domain.mapper.service.MapperService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RoomServiceImpl implements RoomService {
    private final RoomRepository repository;
    private final MapperService mapper;

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
}
