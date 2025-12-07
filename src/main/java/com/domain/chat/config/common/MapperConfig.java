package com.domain.chat.config.common;

import com.domain.chat.mapper.Mapping;
import com.domain.mapper.service.MapperService;
import com.domain.mapper.service.impl.MapperServiceImpl;
import jakarta.persistence.EntityManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MapperConfig {
    @Bean
    public MapperService mapperService(Mapping mapping, EntityManager entityManager) {
        return new MapperServiceImpl(mapping, entityManager);
    }
}
