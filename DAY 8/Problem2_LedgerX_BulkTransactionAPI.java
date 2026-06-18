

package com.ledgerx;

import jakarta.validation.*;
import jakarta.validation.constraints.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.lang.annotation.*;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;


@Documented
@Constraint(validatedBy = SupportedCurrencyValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@interface SupportedCurrency {
    String message() default "Currency not supported. Allowed: USD, EUR, GBP";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}


class SupportedCurrencyValidator implements ConstraintValidator<SupportedCurrency, String> {

    private static final Set<String> SUPPORTED = Set.of("USD", "EUR", "GBP");

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) return false;
        return SUPPORTED.contains(value.toUpperCase());
    }
}


class TransactionDTO {

    @Positive(message = "Amount must be greater than 0")
    private BigDecimal amount;

    @SupportedCurrency 
    private String currency;

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
}

class BatchRequestDTO {

    @NotBlank(message = "Batch ID cannot be blank")
    private String batchId;

    @Valid
    @NotNull(message = "Transactions list cannot be null")
    @Size(min = 1, message = "Transactions list cannot be empty")
    private List<TransactionDTO> transactions;

    public String getBatchId() { return batchId; }
    public void setBatchId(String batchId) { this.batchId = batchId; }
    public List<TransactionDTO> getTransactions() { return transactions; }
    public void setTransactions(List<TransactionDTO> transactions) { this.transactions = transactions; }
}


@RestController
@RequestMapping("/api/v1/batches")
class BatchController {

    @PostMapping
    public ResponseEntity<String> submitBatch(@Valid @RequestBody BatchRequestDTO batch) {
        System.out.println("Batch " + batch.getBatchId() + " accepted with " +
                           batch.getTransactions().size() + " transactions.");
        return ResponseEntity.status(HttpStatus.ACCEPTED)
                .body("Batch " + batch.getBatchId() + " accepted for processing.");
    }
}


@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(
            MethodArgumentNotValidException ex) {

        Map<String, String> errors = new HashMap<>();

        // This extracts array paths like "transactions[0].amount"
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.put(error.getField(), error.getDefaultMessage());
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }
}


class Problem2Demo {
    public static void main(String[] args) {
        System.out.println("=== Day 8 - Problem 2: LedgerX Bulk Transaction API ===");
        System.out.println();
        System.out.println("Valid Payload:");
        System.out.println("{");
        System.out.println("  \"batchId\": \"BATCH-2024-001\",");
        System.out.println("  \"transactions\": [");
        System.out.println("    {\"amount\": 150.00, \"currency\": \"USD\"},");
        System.out.println("    {\"amount\": 75.50, \"currency\": \"EUR\"}");
        System.out.println("  ]");
        System.out.println("}");
        System.out.println("-> 202 Accepted");
        System.out.println();
        System.out.println("Invalid Payload (negative amount + unsupported currency):");
        System.out.println("{");
        System.out.println("  \"batchId\": \"BATCH-2024-002\",");
        System.out.println("  \"transactions\": [");
        System.out.println("    {\"amount\": -50.00, \"currency\": \"JPY\"}");
        System.out.println("  ]");
        System.out.println("}");
        System.out.println("-> 400 Bad Request:");
        System.out.println("{");
        System.out.println("  \"transactions[0].amount\": \"Amount must be greater than 0\",");
        System.out.println("  \"transactions[0].currency\": \"Currency not supported. Allowed: USD, EUR, GBP\"");
        System.out.println("}");
        System.out.println();
        System.out.println("Key: @Valid on List<TransactionDTO> enables cascaded validation.");
        System.out.println("Without @Valid, Spring validates the List itself but NOT objects inside it.");
    }
}
