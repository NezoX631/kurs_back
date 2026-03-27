package com.planify.planifyspring.main.config

import com.planify.planifyspring.main.common.filters.ApplicationHttpExceptionHandlerFilter
import com.planify.planifyspring.main.common.filters.TrailingSlashHandlerFilter
import com.planify.planifyspring.main.exceptions.security.JwtAccessDeniedHandler
import com.planify.planifyspring.main.exceptions.security.JwtAuthenticationEntryPoint
import com.planify.planifyspring.main.features.auth.domain.utils.filters.JWTAuthFilter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.security.web.session.DisableEncodeUrlFilter

@Configuration
@EnableMethodSecurity
@EnableWebSecurity
class ApplicationSecurityConfig(
    private val jwtAuthFilter: JWTAuthFilter,
    private val authenticationEntryPoint: JwtAuthenticationEntryPoint,
    private val accessDeniedHandler: JwtAccessDeniedHandler
) {
    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .csrf { csrf ->
                csrf.ignoringRequestMatchers(
                    "/h2-console/**",
                    "/auth/**"
                )
            }
            .headers { headers ->
                headers.frameOptions { frameOptions ->
                    frameOptions.disable()
                }
            }
            .exceptionHandling {
                it.authenticationEntryPoint(authenticationEntryPoint)
                it.accessDeniedHandler(accessDeniedHandler)
            }
            .authorizeHttpRequests { auth ->
                // Публичные эндпоинты auth
                auth.requestMatchers(
                    "/auth/login",
                    "/auth/register",
                    "/auth/refresh"
                ).permitAll()

                // H2 Console
                auth.requestMatchers(
                    "/h2-console/**",
                    "/h2-console"
                ).permitAll()

                // Swagger
                auth.requestMatchers(
                    "/v3/api-docs/**",
                    "/swagger-ui/**",
                    "/swagger-ui.html"
                ).permitAll()

                // Все остальные запросы требуют аутентификации
                auth.anyRequest().authenticated()
            }
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter::class.java)
            .addFilterBefore(ApplicationHttpExceptionHandlerFilter(), DisableEncodeUrlFilter::class.java)
            .addFilterAfter(TrailingSlashHandlerFilter(), ApplicationHttpExceptionHandlerFilter::class.java)

        return http.build()
    }
}