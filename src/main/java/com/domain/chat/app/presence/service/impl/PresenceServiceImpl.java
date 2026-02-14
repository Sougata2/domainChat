package com.domain.chat.app.presence.service.impl;

import com.domain.chat.app.presence.service.PresenceService;
import com.domain.chat.app.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PresenceServiceImpl implements PresenceService {
    private final UserRepository userRepository;

    @Override
    @Transactional
    public void updatePresence(LocalDateTime lastSeen, String username) {
        userRepository.updateLastSeen(lastSeen, username);
    }
}
