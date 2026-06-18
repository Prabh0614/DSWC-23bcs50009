
package com.notifyhub;

import jakarta.validation.constraints.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


interface SmsGroup {}
interface EmailGroup {}


class AlertConfigDTO {

    @NotBlank(message = "Alert name is required")
    private String alertName;

    @NotBlank(message = "Message is required")
    private String message;

    @NotBlank(message = "Phone number is required for SMS", groups = SmsGroup.class)
    private String phoneNumber;
    @NotBlank(message = "Email address is required for Email", groups = EmailGroup.class)
    @Email(message = "Must be a valid email", groups = EmailGroup.class)
    private String emailAddress;

    public String getAlertName() { return alertName; }
    public void setAlertName(String alertName) { this.alertName = alertName; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public String getEmailAddress() { return emailAddress; }
    public void setEmailAddress(String emailAddress) { this.emailAddress = emailAddress; }
}


@RestController
@RequestMapping("/api/v1/notifications")
class NotificationController {

    @PostMapping("/sms")
    public ResponseEntity<String> configureSms(
            @Validated(SmsGroup.class) @RequestBody AlertConfigDTO config) {

        System.out.println("SMS alert configured for: " + config.getPhoneNumber());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body("SMS alert '" + config.getAlertName() + "' configured successfully.");
    }

    @PostMapping("/email")
    public ResponseEntity<String> configureEmail(
            @Validated(EmailGroup.class) @RequestBody AlertConfigDTO config) {

        System.out.println("Email alert configured for: " + config.getEmailAddress());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body("Email alert '" + config.getAlertName() + "' configured successfully.");
    }
}


class Problem4Demo {
    public static void main(String[] args) {
        System.out.println("=== Day 8 - Problem 4: NotifyHub Multi-Channel API ===");
        System.out.println();
        System.out.println("Same DTO, different validation rules per endpoint:");
        System.out.println();
        System.out.println("POST /api/v1/notifications/sms (SmsGroup active):");
        System.out.println("{");
        System.out.println("  \"alertName\": \"Server Down Alert\",");
        System.out.println("  \"message\": \"Server is down!\",");
        System.out.println("  \"phoneNumber\": \"+1-555-0100\"");
        System.out.println("}");
        System.out.println("-> phoneNumber is validated (required)");
        System.out.println("-> emailAddress is NOT validated (can be null)");
        System.out.println();
        System.out.println("POST /api/v1/notifications/email (EmailGroup active):");
        System.out.println("{");
        System.out.println("  \"alertName\": \"Daily Report\",");
        System.out.println("  \"message\": \"Your daily report is ready\",");
        System.out.println("  \"emailAddress\": \"user@company.com\"");
        System.out.println("}");
        System.out.println("-> emailAddress is validated (required + @Email)");
        System.out.println("-> phoneNumber is NOT validated (can be null)");
        System.out.println();
        System.out.println("Key: @Validated(SmsGroup.class) replaces @Valid.");
        System.out.println("This avoids creating 15 slightly different DTOs (DTO bloat).");
    }
}
