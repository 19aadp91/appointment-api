package com.medisalud.appointment.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(
    name = "patient",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_patient_document", columnNames = "document_number"),
        @UniqueConstraint(name = "uk_patient_email", columnNames = "email")
    }
)
public class PatientEntity extends BaseAuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "patient_id", updatable = false, nullable = false)
    private UUID patientId;

    @Column(name = "full_name", nullable = false, length = 100)
    private String fullName;

    @Column(name = "document_number", nullable = false, length = 30)
    private String documentNumber;

    @Column(name = "phone", nullable = false, length = 20)
    private String phone;

    @Column(name = "email", nullable = false, length = 150)
    private String email;

    @Column(name = "birth_date")
    private LocalDate birthDate;
}
