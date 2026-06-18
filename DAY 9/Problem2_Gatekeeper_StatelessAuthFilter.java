
package com.gatekeeper;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;


@Component
class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider; 

    public JwtAuthenticationFilter(JwtProvider jwtProvider) {
        this.jwtProvider = jwtProvider;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        try {
            String authHeader = request.getHeader("Authorization");

            if (authHeader != null && authHeader.startsWith("Bearer ")) {

                String token = authHeader.substring(7);

                if (jwtProvider.isTokenValid(token)) {

                    if (SecurityContextHolder.getContext().getAuthentication() == null) {

                        String username = jwtProvider.extractUsername(token);
                        List<String> roles = jwtProvider.extractRoles(token);

                        List<SimpleGrantedAuthority> authorities = roles.stream()
                                .map(SimpleGrantedAuthority::new)
                                .collect(Collectors.toList());

                        UsernamePasswordAuthenticationToken authToken =
                                new UsernamePasswordAuthenticationToken(
                                        username,  
                                        null,       
                                        authorities 
                                );

                        authToken.setDetails(
                                new WebAuthenticationDetailsSource().buildDetails(request));

                        SecurityContextHolder.getContext().setAuthentication(authToken);
                    }
                }
            }
        } catch (Exception e) {
            // Log the error but don't block the filter chain
            System.err.println("JWT Authentication failed: " + e.getMessage());
        }

        filterChain.doFilter(request, response);
    }
}


class JwtProvider {
    public boolean isTokenValid(String token) { return token != null && !token.isEmpty(); }
    public String extractUsername(String token) { return "extracted_user"; }
    public List<String> extractRoles(String token) { return List.of("ROLE_USER"); }
}



class Problem2Demo {
    public static void main(String[] args) {
        System.out.println("=== Day 9 - Problem 2: Gatekeeper Stateless Auth Filter ===");
        System.out.println();
        System.out.println("Flow for each HTTP request:");
        System.out.println("1. Extract 'Authorization' header from HttpServletRequest");
        System.out.println("2. Verify header exists AND starts with 'Bearer '");
        System.out.println("3. Strip 'Bearer ' prefix to get raw JWT");
        System.out.println("4. Validate token (signature + expiration)");
        System.out.println("5. Check SecurityContextHolder.getContext().getAuthentication() == null");
        System.out.println("6. Extract username + roles from JWT Claims");
        System.out.println("7. Create UsernamePasswordAuthenticationToken");
        System.out.println("8. Attach WebAuthenticationDetails");
        System.out.println("9. Set into SecurityContextHolder");
        System.out.println("10. ALWAYS call filterChain.doFilter(request, response)");
        System.out.println();
        System.out.println("CRITICAL: Forgetting filterChain.doFilter() causes the API to HANG!");
        System.out.println("The Servlet waits for a response that never comes.");
    }
}
