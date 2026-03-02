package com.jumbotail.ecommerce_shipping.config;


import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OpenAPI / Swagger UI configuration.
 * Access Swagger UI at: http://localhost:8080/swagger-ui.html
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI ecommerceOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("B2B E-Commerce Shipping Charge Estimator API")
                        .description(
                                "APIs for calculating shipping charges in the Jumbotail B2B marketplace. "
                                        + "Helps Kirana stores discover nearest warehouses and estimate delivery costs."
                        )
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Jumbotail Engineering")
                                .email("shreya.palit@jumbotail.com")
                                .url("https://www.jumbotail.com")
                        )
                        .license(new License()
                                .name("Private")
                        )
                );
    }
}