package com.example.demo.configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.parser.OpenAPIV3Parser;
import io.swagger.v3.parser.core.models.SwaggerParseResult;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.nio.file.Files;
import java.nio.file.Paths;

@Configuration
public class OpenAPIConfig {
    @Bean
    public OpenAPI customOpenAPI() {
        try {
            String yamlPath = "src/main/resources/openapi/openapi.yml";
            String yamlContent = new String(Files.readAllBytes(Paths.get(yamlPath)));

            SwaggerParseResult parseResult = new OpenAPIV3Parser().readContents(yamlContent);
            return parseResult.getOpenAPI();
        } catch (Exception e) {
            throw new RuntimeException("Failed to load OpenAPI YAML file", e);
        }
    }
}
