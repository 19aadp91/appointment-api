# Medisalud - API de Gestión de Citas Médicas

Este proyecto consiste en el desarrollo del backend para el sistema de gestión de citas médicas **Medisalud**. La solución ha sido diseñada bajo principios de desarrollo ágil, mantenibilidad y robustez técnica, asegurando el cumplimiento estricto de todas las Reglas de Negocio (RN) solicitadas.

---

## 🏛️ 1. Arquitectura y Justificación Diseño

La aplicación implementa una **Arquitectura Hexagonal (Puertos y Adaptadores)** combinada con patrones tácticos de **Domain-Driven Design (DDD)** como *Handlers*, *Commands* y *Value Objects*.

+---------------------------------------------------+
           |                    INFRAESTRUCTURA                |
           |                                                   |
           |   +-------------+               +-------------+   |
           |   |  REST Ctrls |               | Spring Data |   |
           |   +------+------+               +------+------+   |
           |          |                             ^          |
           +----------|-----------------------------|----------+
                      | (HTTP)                      | (JPA)
           +----------v-----------------------------|----------+
           |          |                             |          |
           |   +------v------+               +------+------+   |
           |   |  Input Port |               | Output Port |   |
           |   | (UseCase UI)|               | (SPI Persist|   |
           |   +------+------+               +------+------+   |
           |          |                             ^          |
           |          | (Invoca)                    | (Usa)    |
           |   +------v-----------------------------+------+   |
           |   |                 DOMINIO                   |   |
           |   |      [ Handlers / Reglas de Negocio ]     |   |
           |   |      [     Modelos / Entidades     ]      |   |
           |   +-------------------------------------------+   |
           |                                                   |
           |                    CORE APLICATIVO                |
           +---------------------------------------------------+

### Justificación de la decisión arquitectónica:
1. **Aislamiento del Core de Negocio:** Las reglas más críticas (como el cálculo de penalizaciones por inasistencia y la restricción estricta de las franjas horarias de 30 minutos) residen en el **Dominio**. No dependen de Spring Boot, Hibernate ni de la base de datos, lo que evita que cambios tecnológicos afecten la lógica de salud.
2. **Alta Testeabilidad:** Al desacoplar las reglas de los controladores web o de la base de datos, es posible ejecutar suites de pruebas unitarias puras y ultrarápidas utilizando mocks de manera nativa.
3. **Atomicidad en Procesos Compuestos:** El caso de uso de *Reprogramación* demuestra la flexibilidad del modelo, logrando orquestar la cancelación de la cita previa y la creación del nuevo bloque horario en una única transacción de infraestructura aislada.

---

## 🛠️ 2. Tecnologías Utilizadas

*   **Java 25:** Última versión con soporte para mejoras de rendimiento y sintaxis moderna (Records, Pattern Matching).
*   **Spring Boot 3.x:** Framework base para la inyección de dependencias, gestión transaccional y exposición de servicios REST.
*   **Spring Data JPA / Hibernate:** Abstracción y control de la capa de persistencia relacional.
*   **PostgreSQL:** Motor de base de datos relacional para entornos productivos y de desarrollo local.
*   **Jakarta Validation:** Validación sintáctica de payloads en el punto de entrada de la API.
*   **Mockito & JUnit 5:** Frameworks seleccionados para la automatización de pruebas unitarias y de comportamiento.

---

## 🚀 3. Instrucciones de Ejecución Local

Prerrequisitos: Tener instalado **Java 25** y **Maven 3.x**. Tener una instancia local de PostgreSQL corriendo con las credenciales configuradas en el archivo `application.properties`.

### Clonar y Compilar el Proyecto
Ejecuta una limpieza, compilación y empaquetamiento completo del artefacto ejecutable:
```bash
./mvnw clean package

Ejecutar la Aplicación
Puedes iniciar el servidor local embebido en el puerto por defecto (8080) a través del wrapper de Maven:

./mvnw spring-boot:run

O ejecutando directamente el archivo JAR generado:

java -jar target/medisalud-appointment-0.0.1-SNAPSHOT.jar

4. Catálogo de Endpoints de la API (Ejemplos)
Todos los fallos de negocio y de infraestructura son interceptados globalmente devolviendo códigos de estado HTTP semánticos (400, 404, 409, 500) con una estructura estandarizada de respuesta.

A. Consultar Franjas Horarias Disponibles (RF-04 / Regla Temporal)
Filtra dinámicamente según la jornada laboral: Lunes a Viernes (08:00 a 18:00) y Sábados (08:00 a 13:00). Los domingos se omiten automáticamente.

Método: GET

Ruta: /api/v1/appointments/available-slots?doctorId=d3b07384-d113-49cd-a5d6-8802d8471900&startDate=2026-08-22&endDate=2026-08-22

Response (200 OK):

{
  "success": true,
  "message": "Available time slots retrieved successfully.",
  "data": [
    "2026-08-22T08:00:00",
    "2026-08-22T08:30:00",
    "2026-08-22T09:00:00",
    "2026-08-22T10:00:00"
  ]
}


B. Crear / Agendar Cita Médica (RF-02)
Método: POST

Ruta: /api/v1/appointments

Request Body (JSON):

{
  "patientId": "c8e17812-4211-4091-8177-3e1989011111",
  "doctorId": "d3b07384-d113-49cd-a5d6-8802d8471900",
  "appointmentDatetime": "2026-08-25T14:30:00"
}

Response (201 Created):

{
  "success": true,
  "message": "Appointment scheduled successfully.",
  "data": "a1b2c3d4-e5f6-7a8b-9c0d-1e2f3a4b5c6d"
}


Response en caso de Conflicto de Horario o Penalizaciones (409 Conflict):


{
  "success": false,
  "message": "The doctor is not available at the requested time slot.",
  "data": null
}

C. Cancelar Cita Médica (RF-05)
Si el tiempo faltante para la cita es inferior a 2 horas, el sistema procesará la cancelación y registrará una penalización automática al paciente sin interrumpir el flujo del servicio.

Método: PATCH (o POST según definición de ruta en infraestructura)

Ruta: /api/v1/appointments/a1b2c3d4-e5f6-7a8b-9c0d-1e2f3a4b5c6d/cancel

Response (200 OK):

{
  "success": true,
  "message": "Appointment has been canceled successfully.",
  "data": null
}

D. Reprogramar Cita Médica (RF-06 / RN-06)
Orquesta de forma atómica la validación del nuevo espacio, la cancelación del bloque previo (con validación de penalización) y el alta del nuevo horario. Si un paso falla, se ejecuta un Rollback automático.

Método: POST

Ruta: /api/v1/appointments/reschedule

Request Body (JSON):

{
  "appointmentId": "a1b2c3d4-e5f6-7a8b-9c0d-1e2f3a4b5c6d",
  "newScheduledAt": "2026-08-28T10:00:00"
}

Response (200 OK):


{
  "success": true,
  "message": "Appointment rescheduled successfully.",
  "data": "f8c3d2e1-b4a5-9c8d-7e6f-0a1b2c3d4e5f"
}