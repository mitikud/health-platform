package com.medical.apigateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
public class RouteConfig {
    @Bean
    public RouteLocator routes(RouteLocatorBuilder builder, Environment env) {
//        return builder.routes()
//                .route("auth", r -> r.path("/api/auth/**").uri(env.getProperty("ROUTE_AUTH_URI", "http://auth-service:8081")))
//                .route("user", r -> r.path("/api/users/**").uri(env.getProperty("ROUTE_USER_URI", "http://user-service:8082")))
//                .route("diagnosis", r -> r.path("/api/diagnosis/**").uri(env.getProperty("ROUTE_DIAGNOSIS_URI", "http://diagnosis-service:8083")))
//                .route("medication", r -> r.path("/api/medications/**").uri(env.getProperty("ROUTE_MEDICATION_URI", "http://medication-service:8084")))
//                .route("translation", r -> r.path("/api/translate/**").uri(env.getProperty("ROUTE_TRANSLATION_URI", "http://translation-service:8085")))
//                .route("notification", r -> r.path("/api/notifications/**").uri(env.getProperty("ROUTE_NOTIFICATION_URI", "http://notification-service:8086")))
//                .route("report", r -> r.path("/api/reports/**").uri(env.getProperty("ROUTE_REPORT_URI", "http://report-service:8087")))
//                .build();
//    }
        return builder.routes()
                .route("auth", r -> r.path("/api/auth/**").uri("lb://auth-service"))
                .route("user", r -> r.path("/api/users/**").uri("lb://user-service"))
                .route("diagnosis", r -> r.path("/api/diagnosis/**").uri("lb://diagnosis-service"))
                .route("medication", r -> r.path("/api/medications/**").uri("lb://medication-service"))
                .route("translation", r -> r.path("/api/translate/**").uri("lb://translation-service"))
                .route("notification", r -> r.path("/api/notifications/**").uri("lb://notification-service"))
                .route("report", r -> r.path("/api/reports/**").uri("lb://report-service"))
                .build();
    }
}

