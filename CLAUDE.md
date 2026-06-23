# Proyecto: gestorEstados

## ¿Qué es este proyecto?
Gestor de Estados: reemplazo funcional de Azure DevOps para la gestión de trabajo
ágil (Proyectos → Sprints → Work Items → Tareas) con tablero Kanban, control de
horas (rollup) e historial de cambios de estado para métricas de cycle time.

## Stack tecnológico
<!-- Detectado automáticamente por /init -->
- Frontend: Angular 21 (standalone, signals, zoneless) + Tailwind CSS 4 · Vitest
- Backend: Spring Boot 3.4.4 / Java 21 · Spring Web (REST) · Spring Data JPA / Hibernate · Bean Validation · Lombok · springdoc-openapi (Swagger) · empaquetado `.war`
- Base de datos: PostgreSQL (esquema `gestorestados`, migración SQL en `db/migration/`)
- CI/CD: Azure DevOps
- Repositorio: Azure Repos

## Arquitectura
Monorepo con tres componentes:
- `gestorestadosbackend/` — REST API. Paquetes por feature (`usuario`, `proyecto`,
  `sprint`, `workitem`, `tarea`, `horas`) bajo `com.gio.gestorestados`, más `shared`
  (audit, config, exception, domain, dto, web). Patrón Controller → Service →
  Repository, con DTOs Request/Response y Mapper por feature. Context-path
  `/gestorestados`, endpoints en `/api/v1/...`, Swagger en `/swagger-ui.html`.
- `gestorestadosfrontend/` — SPA Angular feature-based: `core` (config, models, http
  interceptors), `shared/ui` (librería de componentes), `layout` (app-shell) y
  `features/*` (cada una con `data-access` + componentes y `*.routes.ts` lazy).
- `db/` — migración de esquema física (`V1__esquema_gestorestados.sql`, idempotente).

Jerarquía de dominio: Proyecto → Sprint → WorkItem → Tarea → TareaEstadoHistorial.
Horas: capturadas en `tarea`; derivadas y almacenadas (rollup) en `workitem` y `sprint`.

## Entornos
- Desarrollo: perfil `dev` (`application-dev.properties`), front en http://localhost:4200, API en http://localhost:8080/gestorestados/api/v1
- QA / Staging: []
- Producción: perfil `prod` (`application-prod.properties`)

## Módulos del sistema
Ver [docs/modulos.md](docs/modulos.md) para inventario completo y estado actual.

## Convenciones de código
Ver [docs/convenciones.md](docs/convenciones.md)

## Reglas que SIEMPRE aplican
- Leer `docs/modulos.md` antes de tocar cualquier módulo
- Nunca crear código sin revisar si ya existe algo similar
- Respetar el patrón arquitectónico del módulo más cercano
- Cada PBI debe tener Tasks aprobadas en gestorEstados antes de ejecutar
- Marcar cada Task como Done en gestorEstados al completarla
- Al completar todas las Tasks marcar el PBI como Done

## Convención de estilos CSS — OBLIGATORIO (front)
- NUNCA escribir estilos inline (`style=""`) ni bloques `<style>` dentro del HTML
- Todo estilo va en el archivo `.css` del módulo o componente correspondiente
- Si el archivo `.css` no existe, crearlo con el mismo nombre que el template/controller
- Alcance según descripción de la Task:
  - `"actualizar / ajustar / corregir"` → edición parcial: no borrar reglas existentes del `.css`
  - `"nuevo diseño / rediseño / reemplazar"` → reemplazo permitido: reescribir el `.css` completo
- En cualquier caso: los estilos SIEMPRE van en el `.css`, nunca en el HTML

## Equipo
- Dev(s): []
- PO / Funcional: []
- DBA: []

## Integración gestorEstados
- Gestión de work items vía MCP `gio-mcp-gestorestados` (API REST propia; no usar Azure DevOps)
- Proceso: Product Backlog Items (Work Items) + Tasks (Tareas)
- Estados: PENDIENTE · EN_PROCESO · COMPLETADO
- Sprint activo: consultar vía MCP antes de crear Tasks

## Flujo de trabajo estándar
1. Dev asigna PBI al sprint en gestorEstados
2. Dev ejecuta `/gio-analizar-pbi [ID]`
3. Agente lee PBI + CLAUDE.md + docs/ y genera plan con Tasks
4. Dev aprueba el plan
5. Agente registra Tasks en gestorEstados
6. Agente desarrolla task por task marcando cada una como Done
7. Agente marca PBI como Done al finalizar
