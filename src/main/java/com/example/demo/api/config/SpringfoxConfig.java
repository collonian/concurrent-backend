package com.example.demo.api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.builders.RequestParameterBuilder;
import springfox.documentation.service.ParameterType;
import springfox.documentation.service.RequestParameter;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

import java.util.Collections;

@Configuration
public class SpringfoxConfig {

    @Bean
    public Docket api() {

        return new Docket(DocumentationType.SWAGGER_2)
                .globalRequestParameters(Collections.singletonList(userIdHeader()))
                .select()
                .apis(RequestHandlerSelectors.any())
                .paths(PathSelectors.any())
                .build()
                .pathMapping("/")
                ;
    }

    private RequestParameter userIdHeader() {
        return new RequestParameterBuilder()
                .name(HttpHeaderAuthenticationFilter.USER_ID_HEADER)
                .description("User authentication header")
                .in(ParameterType.HEADER)
                .required(true)
                .build();
    }
}
