CREATE TABLE doctor
(
    doctor_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    full_name VARCHAR(100) NOT NULL,

    specialty VARCHAR(100) NOT NULL,

    phone VARCHAR(20),

    email VARCHAR(150),

    version BIGINT NOT NULL DEFAULT 0,

    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    created_by VARCHAR(100),

    updated_by VARCHAR(100),

    deleted BOOLEAN NOT NULL DEFAULT FALSE,

    CONSTRAINT uk_doctor_email UNIQUE (email),

    CONSTRAINT chk_doctor_name
        CHECK (char_length(trim(full_name)) BETWEEN 3 AND 100)
);

CREATE TABLE patient
(
    patient_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    full_name VARCHAR(100) NOT NULL,

    document_number VARCHAR(30) NOT NULL,

    phone VARCHAR(20) NOT NULL,

    email VARCHAR(150) NOT NULL,

    birth_date DATE,

    version BIGINT NOT NULL DEFAULT 0,

    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    created_by VARCHAR(100),

    updated_by VARCHAR(100),

    deleted BOOLEAN NOT NULL DEFAULT FALSE,

    CONSTRAINT uk_patient_document UNIQUE(document_number),

    CONSTRAINT uk_patient_email UNIQUE(email),

    CONSTRAINT chk_patient_name
        CHECK(char_length(trim(full_name)) BETWEEN 3 AND 100)
);

CREATE TABLE appointment
(
    appointment_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    doctor_id UUID NOT NULL,

    patient_id UUID NOT NULL,

    appointment_datetime TIMESTAMP NOT NULL,

    status VARCHAR(20) NOT NULL,

    cancellation_datetime TIMESTAMP,

    version BIGINT NOT NULL DEFAULT 0,

    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    created_by VARCHAR(100),

    updated_by VARCHAR(100),

    deleted BOOLEAN NOT NULL DEFAULT FALSE,

    CONSTRAINT fk_appointment_doctor
        FOREIGN KEY (doctor_id)
        REFERENCES doctor (doctor_id),

    CONSTRAINT fk_appointment_patient
        FOREIGN KEY (patient_id)
        REFERENCES patient (patient_id),

    CONSTRAINT chk_appointment_status
        CHECK
        (
            status IN
            (
                'PROGRAMMED',
                'CANCELLED',
                'ATTENDED'
            )
        )
);

CREATE TABLE penalty
(
    penalty_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    patient_id UUID NOT NULL,

    appointment_id UUID NOT NULL,

    reason VARCHAR(250),

    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_penalty_patient
        FOREIGN KEY (patient_id)
        REFERENCES patient(patient_id),

    CONSTRAINT fk_penalty_appointment
        FOREIGN KEY (appointment_id)
        REFERENCES appointment(appointment_id)
);

CREATE INDEX idx_doctor_specialty
ON doctor(specialty);

CREATE INDEX idx_appointment_doctor
ON appointment(doctor_id);

CREATE INDEX idx_appointment_patient
ON appointment(patient_id);

CREATE INDEX idx_appointment_datetime
ON appointment(appointment_datetime);

CREATE INDEX idx_appointment_status
ON appointment(status);

CREATE INDEX idx_appointment_doctor_datetime
ON appointment
(
    doctor_id,
    appointment_datetime
);

CREATE INDEX idx_appointment_patient_datetime
ON appointment
(
    patient_id,
    appointment_datetime
);

CREATE INDEX idx_penalty_patient
ON penalty(patient_id);

CREATE INDEX idx_penalty_created
ON penalty(created_at);

INSERT INTO doctor
(
    full_name,
    specialty,
    phone,
    email
)
VALUES
(
    'Dra. María González',
    'Cardiología',
    '5551001',
    'maria.gonzalez@medisalud.com'
),
(
    'Dr. Carlos Ruiz',
    'Pediatría',
    '5551002',
    'carlos.ruiz@medisalud.com'
),
(
    'Dra. Ana López',
    'Dermatología',
    '5551003',
    'ana.lopez@medisalud.com'
);

CREATE UNIQUE INDEX ux_appointment_doctor_active
ON appointment (doctor_id, appointment_datetime)
WHERE status = 'PROGRAMMED';

CREATE UNIQUE INDEX ux_appointment_patient_active
ON appointment (patient_id, appointment_datetime)
WHERE status = 'PROGRAMMED';