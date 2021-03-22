package com.example.demo.api.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.zalando.problem.spring.web.advice.security.SecurityProblemSupport;

@Configuration
@EnableWebSecurity
@Import(SecurityProblemSupport.class)
public class WebSecurityConfig  extends WebSecurityConfigurerAdapter {
    @Autowired
    private SecurityProblemSupport problemSupport;

    @Bean
    public HttpHeaderAuthenticationFilter httpHeaderAuthenticationFilter() {
        return new HttpHeaderAuthenticationFilter();
    }
    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http.authorizeRequests()
                .antMatchers("/h2-console", "/h2-console/**").permitAll()
                .antMatchers("/swagger-ui", "/swagger-ui/**", "/swagger-resources/**", "/v2/api-docs").permitAll()
                .and()
                .headers().frameOptions().sameOrigin();
        http
                .csrf().disable()
                .exceptionHandling()
                    .authenticationEntryPoint(problemSupport)
                    .accessDeniedHandler(problemSupport)
                .and()
                .sessionManagement()
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests()
                    .antMatchers(HttpMethod.OPTIONS).permitAll()
                    .anyRequest().authenticated()
        ;

        http.addFilterBefore(httpHeaderAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
    }
}
