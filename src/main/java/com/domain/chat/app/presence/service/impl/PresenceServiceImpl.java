package com.domain.chat.app.presence.service.impl;

import com.domain.chat.app.presence.service.PresenceService;
import com.domain.chat.app.user.entity.UserEntity;
import com.domain.chat.app.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class PresenceServiceImpl implements PresenceService {
    private final UserRepository userRepository;
    private final Map<Long, Boolean> activeUser = new ConcurrentHashMap<>();

    @Override
    @Transactional
    public void updatePresence(LocalDateTime lastSeen, String username) {
        userRepository.updateLastSeen(lastSeen, username);
    }

    @Override
    public void updateVisibility(Boolean active) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity user = userRepository.findByEmail(username).orElseThrow(() -> new EntityNotFoundException("User %s is not found".formatted(username)));
        activeUser.put(user.getId(), active);
    }

    @Override
    public Boolean getActiveUsers(Long userId) {
        return activeUser.get(userId);
    }
}
