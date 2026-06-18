
package com.cleanfail;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


@Component
class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException)
            throws IOException, ServletException {



        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        Map<String, Object> body = new HashMap<>();
        body.put("error", "Unauthorized");
        body.put("message", "Authentication is required to access this resource.");
        body.put("status", 401);

        objectMapper.writeValue(response.getOutputStream(), body);
    }
}



@Component
class CustomAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException accessDeniedException)
            throws IOException, ServletException {

        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);

        Map<String, Object> body = new HashMap<>();
        body.put("error", "Access Denied");
        body.put("message", "You do not have permission to access this resource.");
        body.put("status", 403);

        objectMapper.writeValue(response.getOutputStream(), body);
    }
}


class Problem4Demo {
    public static void main(String[] args) {
        System.out.println("=== Day 9 - Problem 4: Clean-Fail Security Exception Handler ===");
        System.out.println();
        System.out.println("DEFAULT BEHAVIOR (without custom handlers):");
        System.out.println("  401 -> Tomcat redirects to HTML login page");
        System.out.println("  403 -> Spring returns 'Whitelabel Error Page' HTML");
        System.out.println("  -> React/Mobile JSON parsers CRASH on HTML responses!");
        System.out.println();
        System.out.println("WITH CustomAuthenticationEntryPoint (401):");
        System.out.println("{");
        System.out.println("  \"error\": \"Unauthorized\",");
        System.out.println("  \"message\": \"Authentication is required to access this resource.\",");
        System.out.println("  \"status\": 401");
        System.out.println("}");
        System.out.println();
        System.out.println("WITH CustomAccessDeniedHandler (403):");
        System.out.println("{");
        System.out.println("  \"error\": \"Access Denied\",");
        System.out.println("  \"message\": \"You do not have permission to access this resource.\",");
        System.out.println("  \"status\": 403");
        System.out.println("}");
        System.out.println();
        System.out.println("KEY INSIGHT: Security exceptions happen in the Filter Chain,");
        System.out.println("BEFORE the DispatcherServlet. @RestControllerAdvice CANNOT catch them.");
        System.out.println("Must use ObjectMapper to write JSON directly to HttpServletResponse.");
    }
}
