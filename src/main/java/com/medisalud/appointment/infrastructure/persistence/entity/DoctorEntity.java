package com.medisalud.appointment.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(
    name = "doctor",
    uniqueConstraints = @UniqueConstraint(name = "uk_doctor_email", columnNames = "email"),
    indexes = @Index(name = "idx_doctor_specialty", columnList = "specialty")
)
public class DoctorEntity extends BaseAuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "doctor_id", updatable = false, nullable = false)
    private UUID doctorId;

    @Column(name = "full_name", nullable = false, length = 100)
    private String fullName;

    @Column(name = "specialty", nullable = false, length = 100)
    private String specialty;

    @Column(name = "phone", length = 20)
    private String phone;

    @Column(name = "email", length = 150)
    private String email;
}
