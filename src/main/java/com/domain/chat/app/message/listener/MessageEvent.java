package com.domain.chat.app.message.listener;

import com.domain.chat.app.message.entity.MessageEntity;

public record MessageEvent(MessageEntity entity) {
}
