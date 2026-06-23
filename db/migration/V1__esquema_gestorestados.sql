-- =============================================================================
-- V1__esquema_gestorestados.sql
-- Gestor de Estados (reemplazo funcional de Azure DevOps)
-- Tarea T1 — Esquema fisico `gestorestados` (ver docs/PLAN.md §6.6)
--
-- Idempotente (IF NOT EXISTS). Orden por dependencias:
--   proyecto -> usuario -> sprint -> workitem -> tarea -> tarea_estado_historial
--
-- Decisiones de diseno (dentro del margen del plan):
--   * PK uniforme: BIGINT GENERATED ALWAYS AS IDENTITY en las 6 tablas.
--   * estado: VARCHAR(20) + CHECK IN ('PENDIENTE','EN_PROCESO','COMPLETADO'),
--             DEFAULT 'PENDIENTE' en sprint/workitem/tarea (enum Java STRING).
--   * horas: NUMERIC(8,2) NOT NULL DEFAULT 0. Capturada en `tarea`;
--            DERIVADA pero ALMACENADA (rollup) en `workitem` y `sprint`.
--   * Auditoria: fecha_creacion / fecha_actualizacion TIMESTAMPTZ DEFAULT now()
--             (sin triggers; lo mantiene JPA auditing / BaseEntity §6.2).
--   * Naming de constraints: pk_, fk_, uq_, ck_, idx_.
-- =============================================================================

CREATE SCHEMA IF NOT EXISTS gestorestados;

-- -----------------------------------------------------------------------------
-- proyecto
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS gestorestados.proyecto (
    id                    BIGINT       GENERATED ALWAYS AS IDENTITY,
    nombre                VARCHAR(255) NOT NULL,
    descripcion           TEXT,
    fecha_creacion        TIMESTAMPTZ  NOT NULL DEFAULT now(),
    fecha_actualizacion   TIMESTAMPTZ  NOT NULL DEFAULT now(),
    CONSTRAINT pk_proyecto PRIMARY KEY (id)
);

-- -----------------------------------------------------------------------------
-- usuario
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS gestorestados.usuario (
    id                    BIGINT       GENERATED ALWAYS AS IDENTITY,
    nombre                VARCHAR(255) NOT NULL,
    email                 VARCHAR(320) NOT NULL,
    activo                BOOLEAN      NOT NULL DEFAULT TRUE,
    fecha_creacion        TIMESTAMPTZ  NOT NULL DEFAULT now(),
    fecha_actualizacion   TIMESTAMPTZ  NOT NULL DEFAULT now(),
    CONSTRAINT pk_usuario PRIMARY KEY (id),
    CONSTRAINT uq_usuario_email UNIQUE (email)
);

-- -----------------------------------------------------------------------------
-- sprint  (FK proyecto_id -> proyecto ON DELETE CASCADE)
-- horas: derivada/almacenada (rollup = Sum workitems), solo lectura via API
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS gestorestados.sprint (
    id                    BIGINT        GENERATED ALWAYS AS IDENTITY,
    proyecto_id           BIGINT        NOT NULL,
    nombre                VARCHAR(255)  NOT NULL,
    descripcion           TEXT,
    fecha_inicio          DATE,
    fecha_fin             DATE,
    estado                VARCHAR(20)   NOT NULL DEFAULT 'PENDIENTE',
    horas                 NUMERIC(8,2)  NOT NULL DEFAULT 0,
    fecha_creacion        TIMESTAMPTZ   NOT NULL DEFAULT now(),
    fecha_actualizacion   TIMESTAMPTZ   NOT NULL DEFAULT now(),
    CONSTRAINT pk_sprint PRIMARY KEY (id),
    CONSTRAINT fk_sprint_proyecto FOREIGN KEY (proyecto_id)
        REFERENCES gestorestados.proyecto (id) ON DELETE CASCADE,
    CONSTRAINT ck_sprint_estado CHECK (estado IN ('PENDIENTE','EN_PROCESO','COMPLETADO'))
);

-- -----------------------------------------------------------------------------
-- workitem  (FK sprint_id CASCADE; FK usuario_id SET NULL)
-- horas: derivada/almacenada (rollup = Sum tareas), solo lectura via API
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS gestorestados.workitem (
    id                    BIGINT        GENERATED ALWAYS AS IDENTITY,
    sprint_id             BIGINT        NOT NULL,
    nombre                VARCHAR(255)  NOT NULL,
    descripcion           TEXT,
    criterios_aceptacion  TEXT,
    horas                 NUMERIC(8,2)  NOT NULL DEFAULT 0,
    usuario_id            BIGINT,
    estado                VARCHAR(20)   NOT NULL DEFAULT 'PENDIENTE',
    fecha_creacion        TIMESTAMPTZ   NOT NULL DEFAULT now(),
    fecha_actualizacion   TIMESTAMPTZ   NOT NULL DEFAULT now(),
    CONSTRAINT pk_workitem PRIMARY KEY (id),
    CONSTRAINT fk_workitem_sprint FOREIGN KEY (sprint_id)
        REFERENCES gestorestados.sprint (id) ON DELETE CASCADE,
    CONSTRAINT fk_workitem_usuario FOREIGN KEY (usuario_id)
        REFERENCES gestorestados.usuario (id) ON DELETE SET NULL,
    CONSTRAINT ck_workitem_estado CHECK (estado IN ('PENDIENTE','EN_PROCESO','COMPLETADO'))
);

-- -----------------------------------------------------------------------------
-- tarea  (FK workitem_id CASCADE; FK usuario_id SET NULL)
-- horas: CAPTURADA (esfuerzo humano). orden: reordenamiento en columna del tablero.
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS gestorestados.tarea (
    id                    BIGINT        GENERATED ALWAYS AS IDENTITY,
    workitem_id           BIGINT        NOT NULL,
    nombre                VARCHAR(255)  NOT NULL,
    descripcion           TEXT,
    criterios_aceptacion  TEXT,
    horas                 NUMERIC(8,2)  NOT NULL DEFAULT 0,
    usuario_id            BIGINT,
    estado                VARCHAR(20)   NOT NULL DEFAULT 'PENDIENTE',
    orden                 INTEGER       NOT NULL DEFAULT 0,
    fecha_creacion        TIMESTAMPTZ   NOT NULL DEFAULT now(),
    fecha_actualizacion   TIMESTAMPTZ   NOT NULL DEFAULT now(),
    CONSTRAINT pk_tarea PRIMARY KEY (id),
    CONSTRAINT fk_tarea_workitem FOREIGN KEY (workitem_id)
        REFERENCES gestorestados.workitem (id) ON DELETE CASCADE,
    CONSTRAINT fk_tarea_usuario FOREIGN KEY (usuario_id)
        REFERENCES gestorestados.usuario (id) ON DELETE SET NULL,
    CONSTRAINT ck_tarea_estado CHECK (estado IN ('PENDIENTE','EN_PROCESO','COMPLETADO'))
);

-- -----------------------------------------------------------------------------
-- tarea_estado_historial  (FK tarea_id CASCADE)
-- estado_anterior nullable (primer registro / creacion); estado_nuevo NOT NULL.
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS gestorestados.tarea_estado_historial (
    id                    BIGINT        GENERATED ALWAYS AS IDENTITY,
    tarea_id              BIGINT        NOT NULL,
    estado_anterior       VARCHAR(20),
    estado_nuevo          VARCHAR(20)   NOT NULL,
    fecha_cambio          TIMESTAMPTZ   NOT NULL DEFAULT now(),
    CONSTRAINT pk_tarea_estado_historial PRIMARY KEY (id),
    CONSTRAINT fk_teh_tarea FOREIGN KEY (tarea_id)
        REFERENCES gestorestados.tarea (id) ON DELETE CASCADE,
    CONSTRAINT ck_teh_estado_anterior CHECK (
        estado_anterior IS NULL OR estado_anterior IN ('PENDIENTE','EN_PROCESO','COMPLETADO')),
    CONSTRAINT ck_teh_estado_nuevo CHECK (
        estado_nuevo IN ('PENDIENTE','EN_PROCESO','COMPLETADO'))
);

-- =============================================================================
-- Indices (§6.6): FKs, email unico (ya via uq_), tablero, cycle time.
-- =============================================================================

-- FKs (la UNIQUE de proyecto/usuario PK ya estan indexadas por la PK)
CREATE INDEX IF NOT EXISTS idx_sprint_proyecto_id        ON gestorestados.sprint (proyecto_id);
CREATE INDEX IF NOT EXISTS idx_workitem_sprint_id        ON gestorestados.workitem (sprint_id);
CREATE INDEX IF NOT EXISTS idx_workitem_usuario_id       ON gestorestados.workitem (usuario_id);
CREATE INDEX IF NOT EXISTS idx_tarea_usuario_id          ON gestorestados.tarea (usuario_id);

-- Tablero Kanban por WorkItem: columnas por estado, reordenamiento por `orden`
CREATE INDEX IF NOT EXISTS idx_tarea_workitem_estado_orden
    ON gestorestados.tarea (workitem_id, estado, orden);

-- Cycle time: lectura del historial por tarea en orden cronologico
CREATE INDEX IF NOT EXISTS idx_teh_tarea_fecha
    ON gestorestados.tarea_estado_historial (tarea_id, fecha_cambio);
