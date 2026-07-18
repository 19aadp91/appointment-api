package com.medisalud.appointment.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(
    name = "penalty",
    indexes = {
        @Index(name = "idx_penalty_patient", columnList = "patient_id"),
        @Index(name = "idx_penalty_created", columnList = "created_at")
    }
)
public class PenaltyEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "penalty_id", updatable = false, nullable = false)
    private UUID penaltyId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "patient_id", nullable = false, foreignKey = @ForeignKey(name = "fk_penalty_patient"))
    private PatientEntity patient;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "appointment_id", nullable = false, foreignKey = @ForeignKey(name = "fk_penalty_appointment"))
    private AppointmentEntity appointment;

    @Column(name = "reason", length = 250)
    private String reason;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}
