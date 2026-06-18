package com.healthsync;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "medical_records")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "record_type", discriminatorType = DiscriminatorType.STRING)
public abstract class MedicalRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String diagnosis;
    private String dateRecorded;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id")
    private Patient patient;

    public MedicalRecord() {}

    public MedicalRecord(String diagnosis, String dateRecorded) {
        this.diagnosis = diagnosis;
        this.dateRecorded = dateRecorded;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getDiagnosis() { return diagnosis; }
    public void setDiagnosis(String diagnosis) { this.diagnosis = diagnosis; }
    public String getDateRecorded() { return dateRecorded; }
    public void setDateRecorded(String dateRecorded) { this.dateRecorded = dateRecorded; }
    public Patient getPatient() { return patient; }
    public void setPatient(Patient patient) { this.patient = patient; }
}
@Entity
@DiscriminatorValue("PRESCRIPTION")
class PrescriptionRecord extends MedicalRecord {

    private String medicationName;
    private String dosage;
    private int durationDays;

    public PrescriptionRecord() {}

    public PrescriptionRecord(String diagnosis, String dateRecorded,
                              String medicationName, String dosage, int durationDays) {
        super(diagnosis, dateRecorded);
        this.medicationName = medicationName;
        this.dosage = dosage;
        this.durationDays = durationDays;
    }

    public String getMedicationName() { return medicationName; }
    public void setMedicationName(String medicationName) { this.medicationName = medicationName; }
    public String getDosage() { return dosage; }
    public void setDosage(String dosage) { this.dosage = dosage; }
    public int getDurationDays() { return durationDays; }
    public void setDurationDays(int durationDays) { this.durationDays = durationDays; }
}
@Entity
@DiscriminatorValue("LAB_RESULT")
class LabResultRecord extends MedicalRecord {

    private String labTestName;
    private String result;
    private String referenceRange;

    public LabResultRecord() {}

    public LabResultRecord(String diagnosis, String dateRecorded,
                           String labTestName, String result, String referenceRange) {
        super(diagnosis, dateRecorded);
        this.labTestName = labTestName;
        this.result = result;
        this.referenceRange = referenceRange;
    }

    public String getLabTestName() { return labTestName; }
    public void setLabTestName(String labTestName) { this.labTestName = labTestName; }
    public String getResult() { return result; }
    public void setResult(String result) { this.result = result; }
    public String getReferenceRange() { return referenceRange; }
    public void setReferenceRange(String referenceRange) { this.referenceRange = referenceRange; }
}

@Entity
@Table(name = "patients")
class Patient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String email;

    @OneToMany(mappedBy = "patient", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MedicalRecord> medicalRecords = new ArrayList<>();

    @OneToOne(mappedBy = "patient", cascade = CascadeType.ALL, orphanRemoval = true)
    private BillingAccount billingAccount;

    public Patient() {}

    public Patient(String name, String email) {
        this.name = name;
        this.email = email;
    }

    public void addMedicalRecord(MedicalRecord record) {
        medicalRecords.add(record);
        record.setPatient(this);
    }

    public void removeMedicalRecord(MedicalRecord record) {
        medicalRecords.remove(record);
        record.setPatient(null);
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public List<MedicalRecord> getMedicalRecords() { return medicalRecords; }
    public void setMedicalRecords(List<MedicalRecord> medicalRecords) { this.medicalRecords = medicalRecords; }
    public BillingAccount getBillingAccount() { return billingAccount; }
    public void setBillingAccount(BillingAccount billingAccount) { this.billingAccount = billingAccount; }
}

@Entity
@Table(name = "billing_accounts")
class BillingAccount {

    @Id
    private Long id;  

    private BigDecimal currentBalance;
    private String billingAddress;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId 
    @JoinColumn(name = "patient_id")
    private Patient patient;

    public BillingAccount() {}

    public BillingAccount(BigDecimal currentBalance, String billingAddress) {
        this.currentBalance = currentBalance;
        this.billingAddress = billingAddress;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public BigDecimal getCurrentBalance() { return currentBalance; }
    public void setCurrentBalance(BigDecimal currentBalance) { this.currentBalance = currentBalance; }
    public String getBillingAddress() { return billingAddress; }
    public void setBillingAddress(String billingAddress) { this.billingAddress = billingAddress; }
    public Patient getPatient() { return patient; }
    public void setPatient(Patient patient) { this.patient = patient; }
}

class PatientBillingSummaryDTO {

    private String patientName;
    private String recordType;
    private BigDecimal currentBalance;

    public PatientBillingSummaryDTO(String patientName, String recordType, BigDecimal currentBalance) {
        this.patientName = patientName;
        this.recordType = recordType;
        this.currentBalance = currentBalance;
    }

    public String getPatientName() { return patientName; }
    public String getRecordType() { return recordType; }
    public BigDecimal getCurrentBalance() { return currentBalance; }

    @Override
    public String toString() {
        return "PatientBillingSummaryDTO{" +
               "patientName='" + patientName + '\'' +
               ", recordType='" + recordType + '\'' +
               ", currentBalance=" + currentBalance +
               '}';
    }
}

public interface PatientRepository extends JpaRepository<Patient, Long> {

    @Query("SELECT new com.healthsync.PatientBillingSummaryDTO(" +
           "p.name, TYPE(mr), ba.currentBalance) " +
           "FROM Patient p " +
           "JOIN p.medicalRecords mr " +
           "JOIN p.billingAccount ba")
    List<PatientBillingSummaryDTO> getPatientBillingSummary();

    List<Patient> findByBillingAccountCurrentBalanceGreaterThanAndMedicalRecordsMedicationNameContaining(
            BigDecimal amount, String medicationName);
}



class Problem1Demo {
    public static void main(String[] args) {
        System.out.println("=== Day 7 - Problem 1: HealthSync Patient Record System ===");
        System.out.println();
        System.out.println("This solution demonstrates:");
        System.out.println("1. SINGLE_TABLE inheritance with @DiscriminatorColumn(name=\"record_type\")");
        System.out.println("   - MedicalRecord (base) -> PrescriptionRecord, LabResultRecord");
        System.out.println("2. Bidirectional @OneToMany between Patient and MedicalRecord");
        System.out.println("   - Owning side: MedicalRecord (@JoinColumn)");
        System.out.println("   - Inverse side: Patient (mappedBy=\"patient\")");
        System.out.println("   - CascadeType.ALL + orphanRemoval for auto-deletion");
        System.out.println("3. @OneToOne with @MapsId between Patient and BillingAccount");
        System.out.println("   - BillingAccount shares the same PK as Patient");
        System.out.println("4. JPQL Constructor Expression: SELECT new ...DTO(...)");
        System.out.println("5. Derived Query traversing deep object graphs");
        System.out.println();


        Patient patient = new Patient("John Doe", "john@example.com");

        PrescriptionRecord rx = new PrescriptionRecord(
            "Hypertension", "2024-01-15", "Lisinopril", "10mg", 30);
        patient.addMedicalRecord(rx);

        LabResultRecord lab = new LabResultRecord(
            "Annual Checkup", "2024-02-20", "CBC", "Normal", "4.5-11.0 K/uL");
        patient.addMedicalRecord(lab);

        BillingAccount billing = new BillingAccount(new BigDecimal("1250.00"), "123 Main St");
        billing.setPatient(patient);
        patient.setBillingAccount(billing);

        System.out.println("Patient: " + patient.getName());
        System.out.println("Medical Records: " + patient.getMedicalRecords().size());
        System.out.println("Billing Balance: $" + patient.getBillingAccount().getCurrentBalance());

        PatientBillingSummaryDTO dto = new PatientBillingSummaryDTO(
            patient.getName(), "PRESCRIPTION", billing.getCurrentBalance());
        System.out.println("DTO: " + dto);
    }
}
