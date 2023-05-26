package com.client.ws.rasmooplus.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer.FrameOptionsConfig;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;

@EnableWebSecurity
@Configuration
public class WebSecurityConfig {

    private static final String[] AUTH_SWAGGER_LIST = {
            "/v3/api-docs/**",
            "/swagger-ui/**",
            "/v2/api-docs/**",
            "/swagger-resources/**"
    };

    @Bean
    BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
            throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http, JwtUtils jwtUtils,
            AuthenticationConfiguration authenticationConfiguration, UserDetailsService userDetailsService,
            AuthenticationEntryPoint authEntryPoint)
            throws Exception {
        http
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(AUTH_SWAGGER_LIST).permitAll()
                        .requestMatchers(HttpMethod.GET, "/subscription-type").permitAll()
                        .requestMatchers(HttpMethod.POST, "/user").permitAll()
                        .requestMatchers("/user-recovery-code/*").permitAll()
                        .requestMatchers(HttpMethod.POST, "/payment/process").permitAll()
                        .requestMatchers(HttpMethod.POST, "/auth").permitAll()
                        .requestMatchers("/auth/recovery-code/*").permitAll()
                        .anyRequest().authenticated())
                .headers(header -> header.frameOptions(FrameOptionsConfig::disable))
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.disable())
                .exceptionHandling(handling -> handling
                        .authenticationEntryPoint(authEntryPoint))
                .sessionManagement(sessionManagement -> sessionManagement
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilter(
                        new JwtAuthenticationFilter(authenticationManager(authenticationConfiguration), jwtUtils))
                .addFilter(
                        new JwtAuthorizationFilter(authenticationManager(authenticationConfiguration), jwtUtils,
                                userDetailsService));

        return http.build();
    }
}