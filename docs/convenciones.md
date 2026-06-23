# Convenciones de Código

<!-- Completar según el stack detectado por /init -->

## General
- Idioma del código: español (dominio: proyecto, sprint, workitem, tarea, usuario)
- Idioma de comentarios: español
- Control de versiones: Git (Azure Repos)
- Commits: convencional (feat:, fix:, refactor:, chore:)

## Frontend
- Framework: Angular 21
- Componentes: standalone (sin NgModule)
- State management: Signals (zoneless)
- Estilos: Tailwind CSS 4 (estilos en `.css`, nunca inline ni `<style>` en HTML)
- HTTP: HttpClient con `inject()`; manejo de errores vía `error.interceptor` + ProblemDetail
- Rutas: lazy loading por feature (`*.routes.ts`)
- Estructura de feature: `data-access/` (services) + componentes + `*.routes.ts`
- Nombrado de archivos: kebab-case
- Nombrado de clases: PascalCase
- Testing: Vitest

## Backend
- Framework: Spring Boot 3.4.4 / Java 21
- Estructura de paquetes: por feature bajo `com.gio.gestorestados` (`usuario`, `proyecto`, `sprint`, `workitem`, `tarea`, `horas`) + `shared`
- Patrón: Controller → Service → Repository, con DTOs Request/Response y Mapper por feature
- Entidades: heredan de `BaseEntity` (auditoría JPA: fecha_creacion / fecha_actualizacion)
- Manejo de errores: `GlobalExceptionHandler` + excepciones de dominio (RecursoNoEncontrado, ReglaNegocio, Conflicto); respuestas ProblemDetail
- Validación: Bean Validation (`@Valid`) en DTOs
- API: REST versionada `/api/v1/...`, context-path `/gestorestados`, documentada con springdoc/OpenAPI
- Lombok para boilerplate
- `spring.jpa.open-in-view=false`

## Base de Datos
- Motor: PostgreSQL (esquema `gestorestados`)
- Migraciones: scripts SQL versionados en `db/migration/` (idempotentes, `IF NOT EXISTS`)
- Nombrado de tablas: snake_case
- Nombrado de columnas: snake_case (Hibernate CamelCaseToUnderscoresNamingStrategy)
- PK: `BIGINT GENERATED ALWAYS AS IDENTITY` uniforme
- Naming de constraints: `pk_`, `fk_`, `uq_`, `ck_`, `idx_`
- Estado: `VARCHAR(20)` con CHECK IN ('PENDIENTE','EN_PROCESO','COMPLETADO')
- Índices: siempre justificar con query real (FKs, tablero, cycle time)

## Coexistencia de tecnologías
- Stack homogéneo y moderno (Angular 21 + Spring Boot 3 + PostgreSQL); sin legacy.

## Lo que NUNCA se hace en este proyecto
- Estilos inline (`style=""`) ni bloques `<style>` en el HTML del front
- Mantener transacciones abiertas en la vista (open-in-view deshabilitado)
- Escribir/modificar horas de rollup directamente (son derivadas: workitem ← tareas, sprint ← workitems)
