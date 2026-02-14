package com.domain.chat.app.presence.service;

import java.time.LocalDateTime;

public interface PresenceService {
    void updatePresence(LocalDateTime lastSeen, String username);
}
