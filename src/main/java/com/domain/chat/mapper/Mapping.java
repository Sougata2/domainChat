package com.domain.chat.mapper;

import com.domain.chat.app.message.dto.MessageDto;
import com.domain.chat.app.message.entity.MessageEntity;
import com.domain.chat.app.role.dto.RoleDto;
import com.domain.chat.app.role.entity.RoleEntity;
import com.domain.chat.app.room.dto.RoomDto;
import com.domain.chat.app.room.entity.RoomEntity;
import com.domain.chat.app.user.dto.UserDto;
import com.domain.chat.app.user.entity.UserEntity;
import com.domain.mapper.references.MasterDto;
import com.domain.mapper.references.MasterEntity;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class Mapping implements com.domain.mapper.mapping.Mapping {
    @Override
    public Map<Class<? extends MasterEntity>, Class<? extends MasterDto>> getEntityDtoMap() {
        return Map.ofEntries(
                Map.entry(UserEntity.class, UserDto.class),
                Map.entry(RoleEntity.class, RoleDto.class),
                Map.entry(MessageEntity.class, MessageDto.class),
                Map.entry(RoomEntity.class, RoomDto.class)
        );
    }

    @Override
    public Map<Class<? extends MasterDto>, Class<? extends MasterEntity>> getDtoEntityMap() {
        return Map.ofEntries(
                Map.entry(UserDto.class, UserEntity.class),
                Map.entry(RoleDto.class, RoleEntity.class),
                Map.entry(MessageDto.class, MessageEntity.class),
                Map.entry(RoomDto.class, RoomEntity.class)
        );
    }
}
