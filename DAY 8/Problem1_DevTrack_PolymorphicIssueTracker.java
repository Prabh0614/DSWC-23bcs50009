
package com.devtrack;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "type"
)
@JsonSubTypes({
    @JsonSubTypes.Type(value = BugReportDTO.class, name = "BUG"),
    @JsonSubTypes.Type(value = FeatureRequestDTO.class, name = "FEATURE")
})
abstract class TicketRequestDTO {

    @NotBlank(message = "Title cannot be blank")
    @Size(max = 100, message = "Title must not exceed 100 characters")
    private String title;

    @NotBlank(message = "Description cannot be blank")
    private String description;

    // Getters and Setters
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}

// Concrete DTO: Bug Report
class BugReportDTO extends TicketRequestDTO {

    @Min(value = 1, message = "Severity must be at least 1")
    @Max(value = 5, message = "Severity must not exceed 5")
    private int severity;

    private String stepsToReproduce;

    public int getSeverity() { return severity; }
    public void setSeverity(int severity) { this.severity = severity; }
    public String getStepsToReproduce() { return stepsToReproduce; }
    public void setStepsToReproduce(String stepsToReproduce) { this.stepsToReproduce = stepsToReproduce; }
}
class FeatureRequestDTO extends TicketRequestDTO {

    @Positive(message = "Business value points must be greater than 0")
    private int businessValuePoints;

    private String targetAudience;

    public int getBusinessValuePoints() { return businessValuePoints; }
    public void setBusinessValuePoints(int businessValuePoints) { this.businessValuePoints = businessValuePoints; }
    public String getTargetAudience() { return targetAudience; }
    public void setTargetAudience(String targetAudience) { this.targetAudience = targetAudience; }
}


class UnsupportedTicketTypeException extends RuntimeException {
    public UnsupportedTicketTypeException(String message) {
        super(message);
    }
}


@RestController
@RequestMapping("/api/v1/tickets")
class TicketController {

    @PostMapping
    public ResponseEntity<String> createTicket(@Valid @RequestBody TicketRequestDTO ticket) {

        // Route based on the runtime type of the deserialized DTO
        if (ticket instanceof BugReportDTO bugReport) {
            System.out.println("Processing Bug Report: " + bugReport.getTitle() +
                             " (Severity: " + bugReport.getSeverity() + ")");
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body("Bug Report created successfully: " + bugReport.getTitle());

        } else if (ticket instanceof FeatureRequestDTO featureRequest) {
            System.out.println("Processing Feature Request: " + featureRequest.getTitle() +
                             " (Value: " + featureRequest.getBusinessValuePoints() + ")");
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body("Feature Request created successfully: " + featureRequest.getTitle());

        } else {
            throw new UnsupportedTicketTypeException(
                "Ticket type '" + ticket.getClass().getSimpleName() + "' is not supported.");
        }
    }
}


@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(
            MethodArgumentNotValidException ex) {

        Map<String, String> errors = new HashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.put(error.getField(), error.getDefaultMessage());
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }

    // Handle unsupported ticket types
    @ExceptionHandler(UnsupportedTicketTypeException.class)
    public ResponseEntity<Map<String, String>> handleUnsupportedTicketType(
            UnsupportedTicketTypeException ex) {

        Map<String, String> error = new HashMap<>();
        error.put("error", ex.getMessage());

        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(error);
    }
}


class Problem1Demo {
    public static void main(String[] args) {
        System.out.println("=== Day 8 - Problem 1: Polymorphic Issue Tracker API ===");
        System.out.println();
        System.out.println("JSON Payloads for single POST /api/v1/tickets endpoint:");
        System.out.println();
        System.out.println("Bug Report:");
        System.out.println("{");
        System.out.println("  \"type\": \"BUG\",");
        System.out.println("  \"title\": \"Login page crashes on Safari\",");
        System.out.println("  \"description\": \"Users cannot log in...\",");
        System.out.println("  \"severity\": 3,");
        System.out.println("  \"stepsToReproduce\": \"1. Open Safari 2. Navigate to login\"");
        System.out.println("}");
        System.out.println();
        System.out.println("Feature Request:");
        System.out.println("{");
        System.out.println("  \"type\": \"FEATURE\",");
        System.out.println("  \"title\": \"Dark Mode support\",");
        System.out.println("  \"description\": \"Users want dark mode...\",");
        System.out.println("  \"businessValuePoints\": 50,");
        System.out.println("  \"targetAudience\": \"Power users\"");
        System.out.println("}");
        System.out.println();
        System.out.println("Invalid Bug (severity 9) -> 400 Bad Request:");
        System.out.println("{\"severity\": \"Severity must not exceed 5\"}");
        System.out.println();
        System.out.println("Deprecated type -> 422 Unprocessable Entity:");
        System.out.println("{\"error\": \"Ticket type 'HOTFIX' is not supported.\"}");
    }
}
