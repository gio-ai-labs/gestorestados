import {
  CdkDrag,
  CdkDragDrop,
  CdkDropList,
  transferArrayItem,
} from '@angular/cdk/drag-drop';
import {
  ChangeDetectionStrategy,
  Component,
  computed,
  effect,
  inject,
  input,
  numberAttribute,
  signal,
} from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { firstValueFrom } from 'rxjs';
import { erroresDeCampo, mensajeDeError } from '../../../core/http/problem-detail.util';
import {
  Estado,
  ESTADO_LABEL,
  ESTADOS,
  HistorialEntrada,
  Sprint,
  SprintTableroItem,
  Tarea,
  Usuario,
  WorkItem,
} from '../../../core/models';
import { ShellService } from '../../../layout/shell.service';
import {
  ButtonDirective,
  ConfirmService,
  EmptyState,
  EstadoChip,
  FormField,
  Modal,
  Spinner,
  ToastService,
} from '../../../shared/ui';
import { TareaService } from '../../tareas/data-access/tarea.service';
import { UsuarioService } from '../../usuarios/data-access/usuario.service';
import { WorkItemService } from '../../workitems/data-access/workitem.service';
import { SprintService } from '../data-access/sprint.service';

/** Una fila del tablero: el workitem y sus tareas ya agrupadas por estado. */
interface FilaWorkitem {
  workitem: SprintTableroItem['workitem'];
  tareas: Record<Estado, Tarea[]>;
  /** Ids de los 3 drop lists de esta fila (para conectarlos entre sí). */
  listaIds: string[];
}

/**
 * Vista unificada del sprint estilo Azure Taskboard.
 * Por cada workitem renderiza una fila con 4 columnas:
 * card del workitem + 3 columnas de estado (PENDIENTE / EN_PROCESO / COMPLETADO).
 * El drag & drop mueve tareas entre columnas de estado del MISMO workitem
 * (persistencia optimista del cambio de estado vía TareaService.cambiarEstado).
 *
 * Además ofrece la gestión completa que antes vivía en `workitems-list` y en
 * el tablero por workitem: CRUD de workitem, CRUD de tarea e historial de estados,
 * reutilizando los servicios de data-access y los componentes de `shared/ui`.
 */
@Component({
  selector: 'app-tablero-sprint',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [
    CdkDropList,
    CdkDrag,
    ReactiveFormsModule,
    EstadoChip,
    Spinner,
    EmptyState,
    Modal,
    FormField,
    ButtonDirective,
  ],
  templateUrl: './tablero-sprint.html',
  styleUrl: './tablero-sprint.css',
  host: {
    '(document:keydown.escape)': 'cerrarSelector()',
  },
})
export class TableroSprint {
  /** Bindeados desde la ruta (withComponentInputBinding). El param llega como string;
   *  numberAttribute lo convierte para que el contrato del input sea realmente number. */
  readonly sprintId = input.required<number, unknown>({ transform: numberAttribute });
  readonly proyectoId = input.required<number, unknown>({ transform: numberAttribute });

  private readonly sprintService = inject(SprintService);
  private readonly tareaService = inject(TareaService);
  private readonly workitemService = inject(WorkItemService);
  private readonly usuarioService = inject(UsuarioService);
  private readonly confirmService = inject(ConfirmService);
  private readonly toast = inject(ToastService);
  private readonly shell = inject(ShellService);
  private readonly router = inject(Router);
  private readonly fb = inject(FormBuilder);

  protected readonly estados = ESTADOS;
  protected readonly estadoLabel = ESTADO_LABEL;

  /** Sprints del proyecto para el selector de cabecera. */
  protected readonly sprintsProyecto = signal<Sprint[]>([]);
  /** Sprint actualmente mostrado (derivado del sprintId activo). */
  protected readonly sprintActivo = computed(() =>
    this.sprintsProyecto().find((s) => s.id === this.sprintId()) ?? null,
  );

  /** Estado abierto/cerrado del dropdown de sprint (selector propio, no <select> nativo). */
  protected readonly selectorAbierto = signal(false);

  /** Clase de color del punto de estado, compartida por trigger y opciones. */
  protected dotClase(estado: Estado): string {
    return estado === 'EN_PROCESO'
      ? 'bg-brand-500'
      : estado === 'COMPLETADO'
        ? 'bg-teal-500'
        : 'bg-slate-400';
  }

  protected readonly columnas: { estado: Estado; titulo: string }[] = [
    { estado: 'PENDIENTE', titulo: 'Pendiente' },
    { estado: 'EN_PROCESO', titulo: 'En proceso' },
    { estado: 'COMPLETADO', titulo: 'Completado' },
  ];

  protected readonly cargando = signal(true);
  protected readonly filas = signal<FilaWorkitem[]>([]);
  protected readonly usuariosActivos = signal<Usuario[]>([]);

  protected readonly hayWorkitems = computed(() => this.filas().length > 0);

  // Modal CRUD de workitem
  protected readonly modalWorkitem = signal(false);
  protected readonly workitemEditando = signal<WorkItem | null>(null);
  protected readonly guardandoWorkitem = signal(false);
  protected readonly errorNombreWorkitem = signal<string | null>(null);

  protected readonly formWorkitem = this.fb.nonNullable.group({
    nombre: ['', [Validators.required, Validators.maxLength(255)]],
    descripcion: [''],
    criteriosAceptacion: [''],
    usuarioId: [null as number | null],
    estado: ['PENDIENTE' as Estado],
  });

  // Modal CRUD de tarea (la tarea se crea/edita dentro de un workitem concreto)
  protected readonly modalTarea = signal(false);
  protected readonly tareaEditando = signal<Tarea | null>(null);
  protected readonly tareaWorkitemId = signal<number | null>(null);
  protected readonly guardandoTarea = signal(false);
  protected readonly errorNombreTarea = signal<string | null>(null);

  protected readonly formTarea = this.fb.nonNullable.group({
    nombre: ['', [Validators.required, Validators.maxLength(255)]],
    descripcion: [''],
    horas: [0, [Validators.min(0)]],
    usuarioId: [null as number | null],
    estado: ['PENDIENTE' as Estado],
  });

  // Modal historial de tarea
  protected readonly historialAbierto = signal(false);
  protected readonly historial = signal<HistorialEntrada[]>([]);
  protected readonly historialTarea = signal<Tarea | null>(null);

  constructor() {
    void this.usuarioService.listar(true).then((u) => this.usuariosActivos.set(u));
    // Recarga el tablero cuando cambia el sprintId (input reactivo).
    effect(() => {
      const id = this.sprintId();
      void this.cargar(id);
    });
    // Carga los sprints del proyecto (selector) y fija título/breadcrumb del shell.
    effect(() => {
      const proyectoId = this.proyectoId();
      void this.cargarContextoProyecto(proyectoId);
    });
    // Actualiza el título del shell cuando cambia el sprint mostrado.
    effect(() => {
      const sprint = this.sprintActivo();
      if (sprint) {
        this.shell.set(sprint.nombre, [
          { label: 'Proyectos', link: '/proyectos' },
          { label: 'Sprints', link: ['/proyectos', this.proyectoId(), 'sprints'] },
          { label: sprint.nombre },
        ]);
      }
    });
  }

  /** Trae los sprints hermanos para poblar el selector de la cabecera. */
  private async cargarContextoProyecto(proyectoId: number): Promise<void> {
    try {
      const sprints = await firstValueFrom(this.sprintService.listarPorProyecto(proyectoId));
      this.sprintsProyecto.set(sprints);
    } catch (err) {
      this.toast.error('No se pudieron cargar los sprints del proyecto. ' + mensajeDeError(err));
    }
  }

  /**
   * Cambio de sprint desde el selector: navega a la ruta del sprint elegido.
   * El effect sobre sprintId recarga las 4 columnas sin recargar la página.
   */
  protected cambiarSprint(sprintId: number): void {
    if (sprintId === this.sprintId()) return;
    void this.router.navigate(['/proyectos', this.proyectoId(), 'sprints', sprintId, 'tablero']);
  }

  protected alternarSelector(): void {
    this.selectorAbierto.update((abierto) => !abierto);
  }

  protected cerrarSelector(): void {
    this.selectorAbierto.set(false);
  }

  /** Selección desde el dropdown propio: cierra el panel y navega al sprint elegido. */
  protected seleccionarSprint(sprintId: number): void {
    this.cerrarSelector();
    this.cambiarSprint(sprintId);
  }

  private async cargar(sprintId: number): Promise<void> {
    this.cargando.set(true);
    try {
      const items = await this.obtenerTablero(sprintId);
      this.filas.set(items.map((item) => this.aFila(item)));
    } catch (err) {
      this.toast.error('No se pudo cargar el tablero del sprint. ' + mensajeDeError(err));
      this.filas.set([]);
    } finally {
      this.cargando.set(false);
    }
  }

  private obtenerTablero(sprintId: number): Promise<SprintTableroItem[]> {
    return firstValueFrom(this.sprintService.obtenerTablero(sprintId));
  }

  /** Agrupa las tareas del workitem por estado, respetando el orden del backend. */
  private aFila(item: SprintTableroItem): FilaWorkitem {
    const tareas: Record<Estado, Tarea[]> = {
      PENDIENTE: [],
      EN_PROCESO: [],
      COMPLETADO: [],
    };
    for (const tarea of item.tareas) {
      tareas[tarea.estado].push(tarea);
    }
    return {
      workitem: item.workitem,
      tareas,
      listaIds: ESTADOS.map((estado) => this.listaId(item.workitem.id, estado)),
    };
  }

  /** Id único del drop list de una columna dentro de una fila (workitem + estado). */
  protected listaId(workitemId: number, estado: Estado): string {
    return `wi-${workitemId}-${estado}`;
  }

  protected totalTareas(fila: FilaWorkitem): number {
    return fila.tareas.PENDIENTE.length + fila.tareas.EN_PROCESO.length + fila.tareas.COMPLETADO.length;
  }

  /**
   * Drag & drop entre columnas de estado del mismo workitem.
   * Optimista: actualiza el modelo local primero y persiste; si falla, recarga.
   */
  protected async soltar(
    event: CdkDragDrop<Tarea[]>,
    fila: FilaWorkitem,
    destino: Estado,
  ): Promise<void> {
    if (event.previousContainer === event.container) {
      // Mismo estado: no se reordena (el orden lo define el backend).
      return;
    }

    const origenArr = [...event.previousContainer.data];
    const destinoArr = [...event.container.data];
    const tarea = origenArr[event.previousIndex];
    const origenEstado = tarea.estado;

    transferArrayItem(origenArr, destinoArr, event.previousIndex, event.currentIndex);
    destinoArr[event.currentIndex] = { ...tarea, estado: destino };

    this.actualizarFila(fila.workitem.id, origenEstado, origenArr, destino, destinoArr);

    try {
      await this.tareaService.cambiarEstado(tarea.id, destino);
    } catch (err) {
      this.toast.error('No se pudo mover la tarea; se revirtió. ' + mensajeDeError(err));
      await this.cargar(this.sprintId());
    }
  }

  /** Reemplaza inmutablemente las columnas afectadas de una fila concreta. */
  private actualizarFila(
    workitemId: number,
    origenEstado: Estado,
    origenArr: Tarea[],
    destinoEstado: Estado,
    destinoArr: Tarea[],
  ): void {
    this.filas.update((filas) =>
      filas.map((fila) => {
        if (fila.workitem.id !== workitemId) {
          return fila;
        }
        return {
          ...fila,
          tareas: {
            ...fila.tareas,
            [origenEstado]: origenArr,
            [destinoEstado]: destinoArr,
          },
        };
      }),
    );
  }

  // --- CRUD de WorkItem (reutiliza WorkItemService) ---

  protected abrirNuevoWorkitem(): void {
    this.workitemEditando.set(null);
    this.errorNombreWorkitem.set(null);
    this.formWorkitem.reset({
      nombre: '',
      descripcion: '',
      criteriosAceptacion: '',
      usuarioId: null,
      estado: 'PENDIENTE',
    });
    this.modalWorkitem.set(true);
  }

  protected abrirEditarWorkitem(w: SprintTableroItem['workitem']): void {
    this.workitemEditando.set(w);
    this.errorNombreWorkitem.set(null);
    this.formWorkitem.reset({
      nombre: w.nombre,
      descripcion: w.descripcion ?? '',
      criteriosAceptacion: w.criteriosAceptacion ?? '',
      usuarioId: w.usuarioId ?? null,
      estado: w.estado,
    });
    this.modalWorkitem.set(true);
  }

  protected async guardarWorkitem(): Promise<void> {
    if (this.formWorkitem.invalid) {
      this.formWorkitem.markAllAsTouched();
      return;
    }
    this.guardandoWorkitem.set(true);
    this.errorNombreWorkitem.set(null);
    const v = this.formWorkitem.getRawValue();
    const req = {
      nombre: v.nombre,
      descripcion: v.descripcion || undefined,
      criteriosAceptacion: v.criteriosAceptacion || undefined,
      usuarioId: v.usuarioId,
      estado: v.estado,
    };
    try {
      const editando = this.workitemEditando();
      if (editando) {
        await this.workitemService.actualizar(editando.id, req);
        this.toast.exito('WorkItem actualizado');
      } else {
        await this.workitemService.crear(this.sprintId(), req);
        this.toast.exito('WorkItem creado');
      }
      this.modalWorkitem.set(false);
      await this.cargar(this.sprintId());
    } catch (err) {
      const campos = erroresDeCampo(err);
      if (campos?.['nombre']) {
        this.errorNombreWorkitem.set(campos['nombre']);
      } else {
        this.toast.error(mensajeDeError(err));
      }
    } finally {
      this.guardandoWorkitem.set(false);
    }
  }

  protected async eliminarWorkitem(w: SprintTableroItem['workitem']): Promise<void> {
    const ok = await this.confirmService.ask({
      titulo: '¿Eliminar workitem?',
      mensaje: `Se eliminará "${w.nombre}" junto con sus tareas. Esta acción no se puede deshacer.`,
      peligro: true,
      confirmar: 'Eliminar',
    });
    if (!ok) return;
    try {
      await this.workitemService.eliminar(w.id);
      this.toast.exito('WorkItem eliminado');
      await this.cargar(this.sprintId());
    } catch (err) {
      this.toast.error(mensajeDeError(err));
    }
  }

  // --- CRUD de Tarea (reutiliza TareaService) ---

  protected abrirNuevaTarea(workitemId: number): void {
    this.tareaEditando.set(null);
    this.tareaWorkitemId.set(workitemId);
    this.errorNombreTarea.set(null);
    this.formTarea.reset({
      nombre: '',
      descripcion: '',
      horas: 0,
      usuarioId: null,
      estado: 'PENDIENTE',
    });
    this.modalTarea.set(true);
  }

  protected abrirEditarTarea(t: Tarea, ev: Event): void {
    ev.stopPropagation();
    this.tareaEditando.set(t);
    this.tareaWorkitemId.set(t.workitemId);
    this.errorNombreTarea.set(null);
    this.formTarea.reset({
      nombre: t.nombre,
      descripcion: t.descripcion ?? '',
      horas: t.horas,
      usuarioId: t.usuarioId ?? null,
      estado: t.estado,
    });
    this.modalTarea.set(true);
  }

  protected async guardarTarea(): Promise<void> {
    if (this.formTarea.invalid) {
      this.formTarea.markAllAsTouched();
      return;
    }
    this.guardandoTarea.set(true);
    this.errorNombreTarea.set(null);
    const v = this.formTarea.getRawValue();
    const req = {
      nombre: v.nombre,
      descripcion: v.descripcion || undefined,
      horas: v.horas,
      usuarioId: v.usuarioId,
      estado: v.estado,
    };
    try {
      const editando = this.tareaEditando();
      if (editando) {
        await this.tareaService.actualizar(editando.id, req);
        this.toast.exito('Tarea actualizada');
      } else {
        const workitemId = this.tareaWorkitemId();
        if (workitemId === null) return;
        await this.tareaService.crear(workitemId, req);
        this.toast.exito('Tarea creada');
      }
      this.modalTarea.set(false);
      await this.cargar(this.sprintId());
    } catch (err) {
      const campos = erroresDeCampo(err);
      if (campos?.['nombre']) {
        this.errorNombreTarea.set(campos['nombre']);
      } else {
        this.toast.error(mensajeDeError(err));
      }
    } finally {
      this.guardandoTarea.set(false);
    }
  }

  protected async eliminarTarea(t: Tarea, ev: Event): Promise<void> {
    ev.stopPropagation();
    const ok = await this.confirmService.ask({
      titulo: '¿Eliminar tarea?',
      mensaje: `Se eliminará "${t.nombre}". Esta acción no se puede deshacer.`,
      peligro: true,
      confirmar: 'Eliminar',
    });
    if (!ok) return;
    try {
      await this.tareaService.eliminar(t.id);
      this.toast.exito('Tarea eliminada');
      await this.cargar(this.sprintId());
    } catch (err) {
      this.toast.error(mensajeDeError(err));
    }
  }

  protected async verHistorial(t: Tarea, ev: Event): Promise<void> {
    ev.stopPropagation();
    this.historialTarea.set(t);
    this.historial.set([]);
    this.historialAbierto.set(true);
    try {
      this.historial.set(await this.tareaService.historial(t.id));
    } catch (err) {
      this.toast.error(mensajeDeError(err));
    }
  }
}
