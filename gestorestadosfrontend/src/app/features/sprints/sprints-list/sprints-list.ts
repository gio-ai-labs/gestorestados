import {
  ChangeDetectionStrategy,
  Component,
  computed,
  effect,
  inject,
  input,
  signal,
} from '@angular/core';
import { DatePipe } from '@angular/common';
import {
  AbstractControl,
  FormBuilder,
  ReactiveFormsModule,
  ValidationErrors,
  Validators,
} from '@angular/forms';
import { Router } from '@angular/router';
import { erroresDeCampo, mensajeDeError } from '../../../core/http/problem-detail.util';
import { Estado, ESTADO_LABEL, ESTADOS, Proyecto, Sprint } from '../../../core/models';
import { ProyectoService } from '../../proyectos/data-access/proyecto.service';
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
import { SprintService } from '../data-access/sprint.service';

/** Valida que fechaFin no sea anterior a fechaInicio (cuando ambas estan presentes). */
function rangoFechas(group: AbstractControl): ValidationErrors | null {
  const ini = group.get('fechaInicio')?.value;
  const fin = group.get('fechaFin')?.value;
  return ini && fin && fin < ini ? { rangoFechas: true } : null;
}

@Component({
  selector: 'app-sprints-list',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [
    ReactiveFormsModule,
    DatePipe,
    ButtonDirective,
    EmptyState,
    EstadoChip,
    Spinner,
    Modal,
    FormField,
  ],
  templateUrl: './sprints-list.html',
  styleUrl: './sprints-list.css',
  host: {
    '(document:keydown.escape)': 'cerrarSelector()',
  },
})
export class SprintsList {
  /**
   * Proyecto preseleccionado desde la ruta `:proyectoId` (acceso desde Proyectos,
   * vía withComponentInputBinding). Opcional: en la vista compartida sin proyecto
   * en la ruta llega `undefined` y se usa el primer proyecto disponible por defecto.
   */
  readonly proyectoId = input<string>();

  protected readonly service = inject(SprintService);
  protected readonly proyectoService = inject(ProyectoService);
  private readonly shell = inject(ShellService);
  private readonly toast = inject(ToastService);
  private readonly confirmService = inject(ConfirmService);
  private readonly router = inject(Router);
  private readonly fb = inject(FormBuilder);

  protected readonly estados = ESTADOS;
  protected readonly estadoLabel = ESTADO_LABEL;

  /**
   * Proyecto activo (id) sobre el que opera la vista. Lo fija el selector o la
   * inicialización; al cambiar recarga sprints y encabezado. `null` mientras no
   * hay ningún proyecto resuelto todavía.
   */
  protected readonly proyectoSeleccionadoId = signal<number | null>(null);

  /** Proyecto activo completo, derivado del id seleccionado y la lista cargada. */
  protected readonly proyectoActivo = computed<Proyecto | null>(
    () => this.proyectoService.proyectos().find((p) => p.id === this.proyectoSeleccionadoId()) ?? null,
  );

  /** Estado abierto/cerrado del dropdown de proyecto (selector propio, no <select> nativo). */
  protected readonly selectorAbierto = signal(false);

  protected readonly modalAbierto = signal(false);
  protected readonly editando = signal<Sprint | null>(null);
  protected readonly guardando = signal(false);
  protected readonly errorNombre = signal<string | null>(null);

  protected readonly form = this.fb.nonNullable.group(
    {
      nombre: ['', [Validators.required, Validators.maxLength(255)]],
      descripcion: [''],
      fechaInicio: [''],
      fechaFin: [''],
      estado: ['PENDIENTE' as Estado],
    },
    { validators: rangoFechas },
  );

  constructor() {
    // Asegura que la lista de proyectos esté cargada (reutiliza ProyectoService).
    if (this.proyectoService.proyectos().length === 0) {
      void this.proyectoService.cargar();
    }
    // Resuelve el proyecto inicial de forma reactiva: el input `proyectoId` viene
    // de la ruta y solo está disponible DESPUÉS del constructor (component input
    // binding), por eso se lee dentro de un effect y no en el constructor. Se
    // siembra una sola vez, mientras no haya proyecto seleccionado todavía.
    effect(() => {
      if (this.proyectoSeleccionadoId() !== null) return;
      const proyectos = this.proyectoService.proyectos();
      if (proyectos.length === 0) return;
      const idRuta = this.proyectoId() ? Number(this.proyectoId()) : null;
      const preseleccionado =
        idRuta !== null && proyectos.some((p) => p.id === idRuta) ? idRuta : null;
      this.proyectoSeleccionadoId.set(preseleccionado ?? proyectos[0]?.id ?? null);
    });
    // Al cambiar el proyecto activo: recarga sprints y actualiza encabezado/breadcrumb.
    effect(() => {
      const id = this.proyectoSeleccionadoId();
      if (id === null) return;
      void this.service.cargar(id);
      this.actualizarEncabezado();
    });
  }

  private pid(): number | null {
    return this.proyectoSeleccionadoId();
  }

  /** Fija título y breadcrumb del shell según el proyecto activo. */
  private actualizarEncabezado(): void {
    const proyecto = this.proyectoActivo();
    if (proyecto) {
      this.shell.set(proyecto.nombre, [
        { label: 'Proyectos', link: '/proyectos' },
        { label: proyecto.nombre },
      ]);
    } else {
      this.shell.set('Sprints', [{ label: 'Proyectos', link: '/proyectos' }, { label: 'Sprints' }]);
    }
  }

  protected alternarSelector(): void {
    this.selectorAbierto.update((abierto) => !abierto);
  }

  protected cerrarSelector(): void {
    this.selectorAbierto.set(false);
  }

  /** Cambio de proyecto desde el dropdown: cierra el panel y recarga vía effect. */
  protected seleccionarProyecto(id: number): void {
    this.cerrarSelector();
    if (id === this.proyectoSeleccionadoId()) return;
    this.proyectoSeleccionadoId.set(id);
  }

  protected abrirNuevo(): void {
    this.editando.set(null);
    this.errorNombre.set(null);
    this.form.reset({ nombre: '', descripcion: '', fechaInicio: '', fechaFin: '', estado: 'PENDIENTE' });
    this.modalAbierto.set(true);
  }

  protected abrirEditar(s: Sprint, ev: Event): void {
    ev.stopPropagation();
    this.editando.set(s);
    this.errorNombre.set(null);
    this.form.reset({
      nombre: s.nombre,
      descripcion: s.descripcion ?? '',
      fechaInicio: s.fechaInicio ?? '',
      fechaFin: s.fechaFin ?? '',
      estado: s.estado,
    });
    this.modalAbierto.set(true);
  }

  protected abrir(s: Sprint): void {
    const pid = this.pid();
    if (pid === null) return;
    void this.router.navigate(['/proyectos', pid, 'sprints', s.id, 'tablero']);
  }

  protected async guardar(): Promise<void> {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    const pid = this.pid();
    if (pid === null) return;
    this.guardando.set(true);
    this.errorNombre.set(null);
    const v = this.form.getRawValue();
    const req = {
      nombre: v.nombre,
      descripcion: v.descripcion || undefined,
      fechaInicio: v.fechaInicio || undefined,
      fechaFin: v.fechaFin || undefined,
      estado: v.estado,
    };
    try {
      const editando = this.editando();
      if (editando) {
        await this.service.actualizar(editando.id, req);
        this.toast.exito('Sprint actualizado');
      } else {
        await this.service.crear(pid, req);
        this.toast.exito('Sprint creado');
      }
      this.modalAbierto.set(false);
      await this.service.cargar(pid);
    } catch (err) {
      const campos = erroresDeCampo(err);
      if (campos?.['nombre']) {
        this.errorNombre.set(campos['nombre']);
      } else {
        this.toast.error(mensajeDeError(err));
      }
    } finally {
      this.guardando.set(false);
    }
  }

  protected async eliminar(s: Sprint, ev: Event): Promise<void> {
    ev.stopPropagation();
    const ok = await this.confirmService.ask({
      titulo: '¿Eliminar sprint?',
      mensaje: `Se eliminará "${s.nombre}" junto con sus workitems y tareas. Esta acción no se puede deshacer.`,
      peligro: true,
      confirmar: 'Eliminar',
    });
    if (!ok) return;
    const pid = this.pid();
    if (pid === null) return;
    try {
      await this.service.eliminar(s.id);
      this.toast.exito('Sprint eliminado');
      await this.service.cargar(pid);
    } catch (err) {
      this.toast.error(mensajeDeError(err));
    }
  }
}
