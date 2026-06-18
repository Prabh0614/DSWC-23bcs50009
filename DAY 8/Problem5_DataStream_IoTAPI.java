
package com.datastream;

import com.fasterxml.jackson.databind.JsonMappingException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.util.HashMap;
import java.util.Map;


class TelemetryDTO {

    @NotBlank(message = "Sensor ID is required")
    private String sensorId;

    @NotNull(message = "Temperature is required")
    private Double temperature;

    public String getSensorId() { return sensorId; }
    public void setSensorId(String sensorId) { this.sensorId = sensorId; }
    public Double getTemperature() { return temperature; }
    public void setTemperature(Double temperature) { this.temperature = temperature; }
}


@RestController
@RequestMapping("/api/v1/telemetry")
class TelemetryController {

    @PostMapping
    public ResponseEntity<String> ingestTelemetry(@Valid @RequestBody TelemetryDTO data) {
        System.out.println("Sensor " + data.getSensorId() + " -> Temp: " + data.getTemperature());
        return ResponseEntity.ok("Telemetry from " + data.getSensorId() + " recorded.");
    }
}


@RestControllerAdvice
class GlobalExceptionHandler {

    // Handle validation errors (@Valid)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(
            MethodArgumentNotValidException ex) {

        Map<String, String> errors = new HashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.put(error.getField(), error.getDefaultMessage());
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, String>> handleMalformedPayload(
            HttpMessageNotReadableException ex) {

        Map<String, String> error = new HashMap<>();
        error.put("error", "Malformed JSON payload");

        String detail = ex.getMostSpecificCause().getMessage();
        if (detail != null && detail.length() > 200) {
            detail = detail.substring(0, 200) + "...";
        }
        error.put("detail", detail != null ? detail : "Unable to parse request body");

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }
}


class Problem5Demo {
    public static void main(String[] args) {
        System.out.println("=== Day 8 - Problem 5: DataStream IoT API ===");
        System.out.println();
        System.out.println("Valid Payload:");
        System.out.println("{\"sensorId\": \"SENSOR-42\", \"temperature\": 23.5}");
        System.out.println("-> 200 OK: \"Telemetry from SENSOR-42 recorded.\"");
        System.out.println();
        System.out.println("Type Mismatch (string instead of double):");
        System.out.println("{\"sensorId\": \"SENSOR-42\", \"temperature\": \"HOT\"}");
        System.out.println("-> 400 Bad Request:");
        System.out.println("{");
        System.out.println("  \"error\": \"Malformed JSON payload\",");
        System.out.println("  \"detail\": \"Cannot deserialize value of type `java.lang.Double`...\"");
        System.out.println("}");
        System.out.println();
        System.out.println("Syntax Error (missing closing brace):");
        System.out.println("{\"sensorId\": \"SENSOR-42\", \"temperature\": 23.5");
        System.out.println("-> 400 Bad Request:");
        System.out.println("{");
        System.out.println("  \"error\": \"Malformed JSON payload\",");
        System.out.println("  \"detail\": \"Unexpected end-of-input...\"");
        System.out.println("}");
        System.out.println();
        System.out.println("Key: HttpMessageNotReadableException fires BEFORE the controller.");
        System.out.println("@Valid never runs if Jackson can't parse the JSON first.");
        System.out.println("Without this handler, the API returns a raw HTML stack trace (500 error).");
    }
}
