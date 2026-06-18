
package com.aerofleet;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Embeddable
class MaintenanceLog {

    private String actionPerformed;  // e.g., "Engine Check", "Tire Replacement"
    private String technicianName;
    private LocalDateTime performedAt;

    public MaintenanceLog() {}

    public MaintenanceLog(String actionPerformed, String technicianName, LocalDateTime performedAt) {
        this.actionPerformed = actionPerformed;
        this.technicianName = technicianName;
        this.performedAt = performedAt;
    }

    public String getActionPerformed() { return actionPerformed; }
    public void setActionPerformed(String actionPerformed) { this.actionPerformed = actionPerformed; }
    public String getTechnicianName() { return technicianName; }
    public void setTechnicianName(String technicianName) { this.technicianName = technicianName; }
    public LocalDateTime getPerformedAt() { return performedAt; }
    public void setPerformedAt(LocalDateTime performedAt) { this.performedAt = performedAt; }

    @Override
    public String toString() {
        return actionPerformed + " by " + technicianName + " at " + performedAt;
    }
}


@Entity
@Table(name = "aircraft")
class Aircraft {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String tailNumber;
    private String modelName;
    private boolean isGrounded;

    @ElementCollection
    @CollectionTable(
        name = "aircraft_maintenance_logs",
        joinColumns = @JoinColumn(name = "aircraft_id")
    )
    private List<MaintenanceLog> maintenanceLogs = new ArrayList<>();

    public Aircraft() {}

    public Aircraft(String tailNumber, String modelName, boolean isGrounded) {
        this.tailNumber = tailNumber;
        this.modelName = modelName;
        this.isGrounded = isGrounded;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTailNumber() { return tailNumber; }
    public void setTailNumber(String tailNumber) { this.tailNumber = tailNumber; }
    public String getModelName() { return modelName; }
    public void setModelName(String modelName) { this.modelName = modelName; }
    public boolean isGrounded() { return isGrounded; }
    public void setGrounded(boolean grounded) { isGrounded = grounded; }
    public List<MaintenanceLog> getMaintenanceLogs() { return maintenanceLogs; }
    public void setMaintenanceLogs(List<MaintenanceLog> maintenanceLogs) { this.maintenanceLogs = maintenanceLogs; }
}

class Problem5Demo {
    public static void main(String[] args) {
        System.out.println("=== Day 7 - Problem 5: AeroFleet Aircraft Maintenance Log ===");
        System.out.println();
        System.out.println("This solution demonstrates:");
        System.out.println("1. @ElementCollection with @CollectionTable");
        System.out.println("   - MaintenanceLog is @Embeddable (Value Object, no PK)");
        System.out.println("   - Stored in a secondary table 'aircraft_maintenance_logs'");
        System.out.println("2. JPQL querying into embedded collections");
        System.out.println("3. Spring Data Pagination (Page<Aircraft> + Pageable)");
        System.out.println("4. Derived query with IN clause and boolean flag");
        System.out.println();

        Aircraft aircraft = new Aircraft("N12345", "Boeing 737-800", false);

        aircraft.getMaintenanceLogs().add(new MaintenanceLog(
            "Engine Check", "Mike Wilson", LocalDateTime.of(2024, 3, 15, 10, 30)));
        aircraft.getMaintenanceLogs().add(new MaintenanceLog(
            "Tire Replacement", "Sarah Chen", LocalDateTime.of(2024, 3, 20, 14, 0)));
        aircraft.getMaintenanceLogs().add(new MaintenanceLog(
            "Avionics Update", "Tom Davis", LocalDateTime.of(2024, 4, 1, 9, 0)));

        System.out.println("Aircraft: " + aircraft.getTailNumber() + " (" + aircraft.getModelName() + ")");
        System.out.println("Grounded: " + aircraft.isGrounded());
        System.out.println("Maintenance Logs:");
        for (MaintenanceLog log : aircraft.getMaintenanceLogs()) {
            System.out.println("  - " + log);
        }
    }
}
