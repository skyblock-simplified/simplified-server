package dev.sbs.simplifiedserver.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Defines the OpenAPI metadata bean used by SpringDoc to generate the API specification
 * served at {@code /v3/api-docs} and rendered by the Scalar UI at the root path.
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public @NotNull OpenAPI openAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("SkyBlock Simplified API")
                .description("REST API for SkyBlock Simplified services")
                .version("1.0.0"));
    }

}
