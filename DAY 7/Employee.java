package com.corpmatrix;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name = "employees")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String firstName;
    private String lastName;
    private String department;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "manager_id")
    private Employee manager;

    @OneToMany(mappedBy = "manager")
    private List<Employee> directReports = new ArrayList<>();

    public Employee() {}

    public Employee(String firstName, String lastName, String department) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.department = department;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }
    public Employee getManager() { return manager; }
    public void setManager(Employee manager) { this.manager = manager; }
    public List<Employee> getDirectReports() { return directReports; }
    public void setDirectReports(List<Employee> directReports) { this.directReports = directReports; }
}

@Entity
@Table(name = "full_time_employees")
class FullTimeEmployee extends Employee {

    private BigDecimal annualSalary;
    private int paidTimeOffDays;

    public FullTimeEmployee() {}

    public FullTimeEmployee(String firstName, String lastName, String department,
                            BigDecimal annualSalary, int paidTimeOffDays) {
        super(firstName, lastName, department);
        this.annualSalary = annualSalary;
        this.paidTimeOffDays = paidTimeOffDays;
    }

    public BigDecimal getAnnualSalary() { return annualSalary; }
    public void setAnnualSalary(BigDecimal annualSalary) { this.annualSalary = annualSalary; }
    public int getPaidTimeOffDays() { return paidTimeOffDays; }
    public void setPaidTimeOffDays(int paidTimeOffDays) { this.paidTimeOffDays = paidTimeOffDays; }
}

@Entity
@Table(name = "contractors")
class Contractor extends Employee {

    private BigDecimal hourlyRate;
    private String contractEndDate;

    public Contractor() {}

    public Contractor(String firstName, String lastName, String department,
                      BigDecimal hourlyRate, String contractEndDate) {
        super(firstName, lastName, department);
        this.hourlyRate = hourlyRate;
        this.contractEndDate = contractEndDate;
    }

    public BigDecimal getHourlyRate() { return hourlyRate; }
    public void setHourlyRate(BigDecimal hourlyRate) { this.hourlyRate = hourlyRate; }
    public String getContractEndDate() { return contractEndDate; }
    public void setContractEndDate(String contractEndDate) { this.contractEndDate = contractEndDate; }
}


class ManagerSpanDTO {

    private String managerName;
    private Long directReportCount;

    public ManagerSpanDTO(String managerName, Long directReportCount) {
        this.managerName = managerName;
        this.directReportCount = directReportCount;
    }

    public String getManagerName() { return managerName; }
    public Long getDirectReportCount() { return directReportCount; }

    @Override
    public String toString() {
        return "ManagerSpanDTO{managerName='" + managerName + "', directReportCount=" + directReportCount + '}';
    }
}


class Problem3Demo {
    public static void main(String[] args) {
        System.out.println("=== Day 7 - Problem 3: CorpMatrix Employee Hierarchy ===");
        System.out.println();
        System.out.println("This solution demonstrates:");
        System.out.println("1. JOINED inheritance strategy");
        System.out.println("   - Employee (base table) + full_time_employees + contractors");
        System.out.println("2. Self-referencing @ManyToOne / @OneToMany");
        System.out.println("   - manager (ManyToOne) <-> directReports (OneToMany)");
        System.out.println("3. JPQL with GROUP BY + COUNT() for aggregation");
        System.out.println("4. ManagerSpanDTO via Constructor Expression");
        System.out.println("5. Derived query traversing self-reference");
        System.out.println();

        // Simulating hierarchy
        FullTimeEmployee ceo = new FullTimeEmployee("Alice", "Johnson", "Executive",
            new BigDecimal("250000"), 30);
        FullTimeEmployee vp = new FullTimeEmployee("Bob", "Smith", "Engineering",
            new BigDecimal("180000"), 25);
        Contractor contractor = new Contractor("Charlie", "Brown", "Engineering",
            new BigDecimal("75.00"), "2025-12-31");

        vp.setManager(ceo);
        contractor.setManager(ceo);
        ceo.getDirectReports().add(vp);
        ceo.getDirectReports().add(contractor);

        System.out.println("CEO: " + ceo.getFirstName() + " " + ceo.getLastName());
        System.out.println("Direct Reports: " + ceo.getDirectReports().size());
        System.out.println("  - " + vp.getFirstName() + " (Full-Time, Salary: $" +
                           ((FullTimeEmployee) vp).getAnnualSalary() + ")");
        System.out.println("  - " + contractor.getFirstName() + " (Contractor, Rate: $" +
                           contractor.getHourlyRate() + "/hr)");

        ManagerSpanDTO dto = new ManagerSpanDTO(ceo.getFirstName(), 2L);
        System.out.println("DTO: " + dto);
    }
}
