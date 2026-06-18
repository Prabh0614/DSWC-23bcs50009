
package com.secureauth;

import jakarta.validation.*;
import jakarta.validation.constraints.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.lang.annotation.*;
import java.util.HashMap;
import java.util.Map;


@Documented
@Constraint(validatedBy = PasswordsMatchValidator.class)
@Target({ElementType.TYPE})  // Applied at CLASS level, not field
@Retention(RetentionPolicy.RUNTIME)
@interface PasswordsMatch {
    String message() default "Passwords do not match";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}


class PasswordsMatchValidator implements ConstraintValidator<PasswordsMatch, RegistrationRequestDTO> {

    @Override
    public boolean isValid(RegistrationRequestDTO dto, ConstraintValidatorContext context) {
        if (dto.getPassword() == null || dto.getConfirmPassword() == null) {
            return false;
        }
        return dto.getPassword().equals(dto.getConfirmPassword());
    }
}



@PasswordsMatch  
class RegistrationRequestDTO {

    @NotBlank(message = "Username cannot be blank")
    @Size(min = 3, max = 30, message = "Username must be between 3 and 30 characters")
    private String username;

    @NotBlank(message = "Email cannot be blank")
    @Email(message = "Email must be a valid email address")
    private String email;

    @NotBlank(message = "Password cannot be blank")
    @Size(min = 8, message = "Password must be at least 8 characters")
    private String password;

    @NotBlank(message = "Confirm password cannot be blank")
    private String confirmPassword;

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getConfirmPassword() { return confirmPassword; }
    public void setConfirmPassword(String confirmPassword) { this.confirmPassword = confirmPassword; }
}



class UserResponseDTO {

    private String username;
    private String email;
    private String status;

    public UserResponseDTO(String username, String email, String status) {
        this.username = username;
        this.email = email;
        this.status = status;
    }

    public String getUsername() { return username; }
    public String getEmail() { return email; }
    public String getStatus() { return status; }
}


@RestController
@RequestMapping("/api/v1/auth")
class AuthController {

    @PostMapping("/register")
    public ResponseEntity<UserResponseDTO> register(
            @Valid @RequestBody RegistrationRequestDTO request) {

        System.out.println("Registering user: " + request.getUsername());

        UserResponseDTO response = new UserResponseDTO(
            request.getUsername(),
            request.getEmail(),
            "ACTIVE"
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}


@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(
            MethodArgumentNotValidException ex) {

        Map<String, String> errors = new HashMap<>();

        // Extract FIELD-level errors (e.g., invalid email)
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.put(error.getField(), error.getDefaultMessage());
        }

        // Extract GLOBAL/CLASS-level errors (e.g., @PasswordsMatch)
        for (ObjectError error : ex.getBindingResult().getGlobalErrors()) {
            errors.put(error.getObjectName(), error.getDefaultMessage());
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }
}


class Problem3Demo {
    public static void main(String[] args) {
        System.out.println("=== Day 8 - Problem 3: SecureAuth Onboarding API ===");
        System.out.println();
        System.out.println("Valid Registration:");
        System.out.println("{");
        System.out.println("  \"username\": \"john_doe\",");
        System.out.println("  \"email\": \"john@example.com\",");
        System.out.println("  \"password\": \"SecureP@ss1\",");
        System.out.println("  \"confirmPassword\": \"SecureP@ss1\"");
        System.out.println("}");
        System.out.println("-> 201 Created: {\"username\": \"john_doe\", \"email\": \"john@example.com\", \"status\": \"ACTIVE\"}");
        System.out.println();
        System.out.println("Mismatched Passwords:");
        System.out.println("{");
        System.out.println("  \"username\": \"jane\",");
        System.out.println("  \"email\": \"invalid-email\",");
        System.out.println("  \"password\": \"12345678\",");
        System.out.println("  \"confirmPassword\": \"87654321\"");
        System.out.println("}");
        System.out.println("-> 400 Bad Request:");
        System.out.println("{");
        System.out.println("  \"email\": \"Email must be a valid email address\",");
        System.out.println("  \"registrationRequestDTO\": \"Passwords do not match\"");
        System.out.println("}");
        System.out.println();
        System.out.println("Key: Field errors are on specific fields, class-level errors are 'global'.");
        System.out.println("Both must be extracted and merged for a complete error response.");
    }
}
