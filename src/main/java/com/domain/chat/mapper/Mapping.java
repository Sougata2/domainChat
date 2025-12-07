package com.domain.chat.mapper;

import com.domain.mapper.references.MasterDto;
import com.domain.mapper.references.MasterEntity;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class Mapping implements com.domain.mapper.mapping.Mapping {
    @Override
    public Map<Class<? extends MasterEntity>, Class<? extends MasterDto>> getEntityDtoMap() {
        return Map.ofEntries();
    }

    @Override
    public Map<Class<? extends MasterDto>, Class<? extends MasterEntity>> getDtoEntityMap() {
        return Map.ofEntries();
    }
}
