package com.wedul.openapi.annotation.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI getOpenApi() {

        return new OpenAPI().components(new Components())
                .info(getInfo());

    }

    private Info getInfo() {
        return new Info()
                .version("1.0.0")
                .description("WE REST API DOC")
                .title("wedul rest api");
    }

    @Bean
    public MethodCustomizer methodCustomizer() {
        return new MethodCustomizer();
    }

}
