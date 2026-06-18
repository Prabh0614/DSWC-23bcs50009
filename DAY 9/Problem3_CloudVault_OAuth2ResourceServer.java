
package com.cloudvault;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;


@Configuration
@EnableWebSecurity
class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
            .csrf(csrf -> csrf.disable())

            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )

            .authorizeHttpRequests(auth -> auth
               
                .requestMatchers("/api/v1/public/**").permitAll()

                .requestMatchers("/api/v1/admin/**").hasAuthority("SCOPE_admin")

                .anyRequest().authenticated()
            )

            .oauth2ResourceServer(oauth2 ->
                oauth2.jwt(jwt -> {
                    
                })
            );

        return http.build();
    }
}


class Problem3Demo {
    public static void main(String[] args) {
        System.out.println("=== Day 9 - Problem 3: CloudVault OAuth2 Resource Server ===");
        System.out.println();
        System.out.println("SecurityFilterChain Configuration:");
        System.out.println();
        System.out.println("1. CSRF: Disabled (token-based, no cookies)");
        System.out.println("2. Sessions: STATELESS (no HttpSession created)");
        System.out.println("3. Route Authorization:");
        System.out.println("   - /api/v1/public/**  -> permitAll()");
        System.out.println("   - /api/v1/admin/**   -> hasAuthority(\"SCOPE_admin\")");
        System.out.println("   - Everything else    -> authenticated()");
        System.out.println("4. OAuth2 Resource Server: JWT validation enabled");
        System.out.println();
        System.out.println("Spring Security 6 Migration Notes:");
        System.out.println("  OLD (deprecated): extends WebSecurityConfigurerAdapter");
        System.out.println("  NEW: @Bean SecurityFilterChain");
        System.out.println();
        System.out.println("  OLD: .csrf().disable()");
        System.out.println("  NEW: .csrf(csrf -> csrf.disable())  // Lambda DSL");
        System.out.println();
        System.out.println("  OLD: .oauth2ResourceServer().jwt()");
        System.out.println("  NEW: .oauth2ResourceServer(o -> o.jwt(j -> {}))");
        System.out.println();
        System.out.println("application.yml:");
        System.out.println("  spring:");
        System.out.println("    security:");
        System.out.println("      oauth2:");
        System.out.println("        resourceserver:");
        System.out.println("          jwt:");
        System.out.println("            issuer-uri: https://your-auth-server.com");
    }
}
