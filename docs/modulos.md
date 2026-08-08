# Inventario de Módulos

<!-- El agente actualiza este archivo automáticamente al completar cada PBI -->

## Estado
- ✅ Completo
- 🔄 En progreso
- ⏳ Pendiente
- ❌ Deprecado

## Frontend

| Módulo | Descripción | Estado | Notas |
|--------|-------------|--------|-------|
| home | Página de inicio / dashboard | ✅ | `features/home/home.ts` |
| proyectos | Listado y gestión de proyectos | ✅ | `features/proyectos/` (data-access + proyectos-list) · `proyectos.routes.ts`. **Paginado server-side (PBI #17):** consume `Pagina<T>` del backend (`?page&size&nombre`), controles `ui-paginacion`, spinner y empty-state |
| sprints | Listado de sprints (vista compartida) + vista unificada del tablero de sprint | ✅ | `features/sprints/` (data-access + sprints-list + **tablero-sprint**) · `sprints.routes.ts` (`:sprintId/tablero`). **`sprints-list` es vista compartida (PBI #11):** accesible vía ruta top-level `/sprints` (menú lateral, abre el primer proyecto por defecto) y vía anidada `/proyectos/:proyectoId/sprints` (preseleccionada); incluye selector de proyecto que recarga lista/título/breadcrumb al cambiar, con `input` `proyectoId` opcional. Tablero: vista única estilo Azure Taskboard: grilla 4 columnas por workitem (workitem + PENDIENTE/EN_PROCESO/COMPLETADO), ids visibles, drag&drop con persistencia de estado, CRUD de workitem/tarea + historial, selector de sprint (PBI #9). **Ajustes UX (PBI #12):** workitems ordenados por id desc, 2 cards de tarea por fila (1 en móvil), clic en card (workitem/tarea) abre edición, secciones de workitem colapsables con control accesible (COMPLETADO cerrado por defecto; PENDIENTE/EN_PROCESO abierto). **Paginado y filtros (PBI #17):** `sprints-list` pagina server-side y filtra por estado del sprint (pills); `tablero-sprint` pagina workitems server-side y filtra por estado del workitem, preservando drag&drop/colapso/selector y la página/filtro tras CRUD; ambas reutilizan `ui-paginacion` y vuelven a página 1 al filtrar |
| workitems | Gestión de work items (sin vista propia) | ✅ | `features/workitems/data-access` — `workitem.service` reutilizado por la vista de sprint. Lista y ruta propias retiradas en PBI #9 (CRUD absorbido por `tablero-sprint`) |
| tareas | Gestión de tareas (sin vista propia) | ✅ | `features/tareas/data-access` — `tarea.service` (CRUD, cambio de estado, historial, cycle time) reutilizado por la vista de sprint. Tablero por workitem retirado en PBI #9 |
| usuarios | Listado y gestión de usuarios | ✅ | `features/usuarios/` (data-access + usuarios-list) · `usuarios.routes.ts` |
| shared/ui | Librería de componentes UI | ✅ | button, card, modal, table, toast, confirm, spinner, form-field, empty-state, estado-chip, **paginacion** (`ui-paginacion`, presentacional: inputs `pagina`/`totalPaginas`/`totalElementos`/`tamano`, output `paginaCambia`) — PBI #17. **Dark mode (PBI #57):** todos los componentes tienen variantes `dark:` aplicadas; modal/toast/confirm migrados de `styles[]` a CSS externo |
| core | Config, models, HTTP interceptors | ✅ | api.config, models/* (incl. `Pagina<T>` — PBI #17), error.interceptor, problem-detail.util. **Dark mode (PBI #57):** `theme.service.ts` — signal de tema activo, persistencia localStorage, fallback `prefers-color-scheme`, aplica/quita clase `.dark` en `<html>` |
| layout | Shell de la aplicación | ✅ | app-shell, shell.service · menú lateral con ítems Proyectos / **Sprints** (PBI #11) / Usuarios (íconos por `@switch` + `routerLinkActive`). **Dark mode (PBI #57):** botón toggle sol/luna en topbar (junto al avatar), sidebar/topbar/main card con clases `dark:`; nav activo usa `.nav-link-active` (light: ink-900, dark: brand-600) |

## Backend

| Módulo | Descripción | Estado | Notas |
|--------|-------------|--------|-------|
| usuario | CRUD de usuarios | ✅ | `/api/v1/usuarios` · entity, dto, mapper, repository, service, controller |
| proyecto | CRUD de proyectos | ✅ | `/api/v1/proyectos` · raíz de la jerarquía. **Listado paginado server-side (PBI #17):** `?page&size&sort&nombre` → `PaginaResponse<T>` (`shared/dto`) |
| sprint | CRUD de sprints + cambio de estado + tablero compuesto | ✅ | `/api/v1/sprints` · horas rollup desde workitems · `GET /api/v1/sprints/{sprintId}/tablero` retorna workitems del sprint con sus tareas (JOIN FETCH, sin N+1) — PBI #9. **Paginado server-side + filtros (PBI #17):** listado `GET /proyectos/{id}/sprints?page&size&nombre&estado` y tablero `?page&size&estado` (workitem) ahora devuelven `PaginaResponse<T>`; paginación de workitems del tablero sin N+1 (2ª consulta de tareas `IN (:ids)`). `GlobalExceptionHandler` mapea `estado` inválido a 400 |
| workitem | CRUD de work items + criterios + cambio de estado | ✅ | `/api/v1/workitems` · horas rollup desde tareas |
| tarea | CRUD de tareas, tablero, reordenamiento, historial, cycle time | ✅ | `/api/v1/tareas` · TareaEstadoHistorial · CycleTimeController |
| horas | Rollup de horas (tarea → workitem → sprint) | ✅ | `HorasRollupService` |
| shared | Auditoría, config (CORS, OpenAPI, JPA), excepciones, dominio (Estado), referencias | ✅ | `BaseEntity`, `GlobalExceptionHandler`, `ReferenciaController` |

## Base de Datos

| Tabla / Esquema | Descripción | Estado | Notas |
|-----------------|-------------|--------|-------|
| gestorestados.proyecto | Proyectos | ✅ | raíz de la jerarquía |
| gestorestados.usuario | Usuarios | ✅ | email único (`uq_usuario_email`) |
| gestorestados.sprint | Sprints | ✅ | FK proyecto (CASCADE) · estado · horas rollup |
| gestorestados.workitem | Work items | ✅ | FK sprint (CASCADE), usuario (SET NULL) · criterios_aceptacion · horas rollup |
| gestorestados.tarea | Tareas | ✅ | FK workitem (CASCADE), usuario (SET NULL) · orden (Kanban) · horas capturadas |
| gestorestados.tarea_estado_historial | Historial de cambios de estado | ✅ | FK tarea (CASCADE) · base del cycle time |

## Integraciones externas

| Integración | Tipo | Estado | Descripción |
|-------------|------|--------|-------------|
| gio-mcp-gestorestados | REST (MCP) | ✅ | Gestión de work items/tareas vía la propia API |
| Swagger UI / OpenAPI | REST | ✅ | Documentación en `/gestorestados/swagger-ui.html` |
