package com.domain.chat.app.user.service.impl;

import com.domain.chat.app.user.dto.UserDto;
import com.domain.chat.app.user.entity.UserEntity;
import com.domain.chat.app.user.repository.UserRepository;
import com.domain.chat.app.user.service.UserService;
import com.domain.chat.component.emitter.EmitterRegistry;
import com.domain.mapper.service.MapperService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final EmitterRegistry emitterRegistry;
    private final UserRepository repository;
    private final MapperService mapper;

    @Override
    public SseEmitter stream() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return emitterRegistry.addEmitter(username);
    }

    @Override
    public List<UserDto> getContacts() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity user = repository.findByEmail(username)
                .orElseThrow(() -> new EntityNotFoundException("User %s is not found".formatted(username)));
        List<UserEntity> entities = repository.findAllExceptLoggedInUser(user.getEmail());
        return entities.stream().map(e -> (UserDto) mapper.toDto(e)).toList();
    }
}
