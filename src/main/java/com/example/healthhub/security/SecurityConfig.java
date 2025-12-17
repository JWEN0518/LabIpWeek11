package com.example.healthhub.security;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    @Bean
    public InMemoryUserDetailsManager userDetailsService() {
        UserDetails admin = User.withUsername("admin")
            .password("{noop}admin1234") //{noop} encryption, not a correct way
            .roles("ADMIN")
            .build();

        UserDetails member = User.withUsername("member")
            .password("{noop}member1234")
            .roles("MEMBER")
            .build();

        UserDetails trainer = User.withUsername("trainer")
            .password("{noop}trainer1234")
            .roles("TRAINER")
            .build();

        return new InMemoryUserDetailsManager(admin, member, trainer);
    }

    /*
    @Bean
    public PasswordEncode passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
    */

    @Bean 
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf().disable() //default is enabled, disable for simplicity

            .authorizeHttpRequests()
            //.requestMatchers("/", "/css/","/js/", "/images/**").permitAll()
            .requestMatchers("/admin/**").hasRole("ADMIN")
            .requestMatchers("/member/**").hasRole("MEMBER")
            .requestMatchers("/trainer/**").hasRole("TRAINER")
            .anyRequest().authenticated()
            .and()
            
            .formLogin()
            .defaultSuccessUrl("/dashboard", true)
            .permitAll()
            .and()

            .logout()
            .logoutSuccessUrl("/login?logout")
            .permitAll();

        return http.build();
    }

}
