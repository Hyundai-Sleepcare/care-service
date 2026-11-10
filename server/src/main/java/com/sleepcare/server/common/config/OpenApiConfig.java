package com.sleepcare.server.common.config;

import com.sleepcare.server.common.owner.OwnerIdInterceptor;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI openAPI() {
        SecurityScheme ownerIdHeader = new SecurityScheme()
                .type(SecurityScheme.Type.APIKEY)
                .in(SecurityScheme.In.HEADER)
                .name(OwnerIdInterceptor.HEADER)
                .description("기기별 UUID v4");
        return new OpenAPI()
                .info(new Info().title("슬립케어 서버 API").version("v1"))
                .components(new Components().addSecuritySchemes(OwnerIdInterceptor.HEADER, ownerIdHeader))
                .addSecurityItem(new SecurityRequirement().addList(OwnerIdInterceptor.HEADER));
    }
}
