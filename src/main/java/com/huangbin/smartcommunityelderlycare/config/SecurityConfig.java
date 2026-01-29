package com.huangbin.smartcommunityelderlycare.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())  // 禁用CSRF保护（API项目通常需要）
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/**").permitAll()  // 允许所有/api/**请求
                        .requestMatchers("/**").permitAll()      // 允许所有请求（测试用）
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form.disable())  // 禁用表单登录
                .httpBasic(httpBasic -> httpBasic.disable()); // 禁用HTTP Basic认证

        return http.build();
    }
}