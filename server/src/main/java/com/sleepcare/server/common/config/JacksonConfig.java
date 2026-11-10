package com.sleepcare.server.common.config;

import org.springframework.boot.jackson.autoconfigure.JsonMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.MapperFeature;

@Configuration
public class JacksonConfig {

    // "2" → 2, 2.5 → 2 같은 자동 변환을 막는다. 잘못된 값은 400이 된다
    @Bean
    public JsonMapperBuilderCustomizer strictNumberCustomizer() {
        return builder -> builder
                .disable(MapperFeature.ALLOW_COERCION_OF_SCALARS)
                .disable(DeserializationFeature.ACCEPT_FLOAT_AS_INT);
    }
}
