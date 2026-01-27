package com.domain.chat.app.message.listener;


import com.domain.chat.app.message.entity.MessageEntity;
import com.domain.chat.config.common.ApplicationContextProvider;
import jakarta.persistence.PostPersist;
import jakarta.persistence.PostUpdate;

public class MessageListener {
    @PostUpdate
    @PostPersist
    public void postPersist(MessageEntity entity) {
        ApplicationContextProvider.publishEvent(new MessageEvent(entity));
    }
}
