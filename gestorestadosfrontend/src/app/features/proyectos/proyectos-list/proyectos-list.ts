import { ChangeDetectionStrategy, Component, inject, OnInit, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { erroresDeCampo, mensajeDeError } from '../../../core/http/problem-detail.util';
import { Proyecto } from '../../../core/models';
import { ShellService } from '../../../layout/shell.service';
import {
  ButtonDirective,
  ConfirmService,
  EmptyState,
  FormField,
  Modal,
  Spinner,
  ToastService,
} from '../../../shared/ui';
import { ProyectoService } from '../data-access/proyecto.service';

@Component({
  selector: 'app-proyectos-list',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [ReactiveFormsModule, ButtonDirective, EmptyState, Spinner, Modal, FormField],
  templateUrl: './proyectos-list.html',
})
export class ProyectosList implements OnInit {
  protected readonly service = inject(ProyectoService);
  private readonly shell = inject(ShellService);
  private readonly confirm = inject(ConfirmService);
  private readonly toast = inject(ToastService);
  private readonly router = inject(Router);
  private readonly fb = inject(FormBuilder);

  protected readonly modalAbierto = signal(false);
  protected readonly editando = signal<Proyecto | null>(null);
  protected readonly guardando = signal(false);
  protected readonly errorNombre = signal<string | null>(null);

  protected readonly form = this.fb.nonNullable.group({
    nombre: ['', [Validators.required, Validators.maxLength(255)]],
    descripcion: [''],
  });

  ngOnInit(): void {
    this.shell.set('Proyectos', [{ label: 'Proyectos' }]);
    void this.service.cargar();
  }

  protected abrirNuevo(): void {
    this.editando.set(null);
    this.errorNombre.set(null);
    this.form.reset({ nombre: '', descripcion: '' });
    this.modalAbierto.set(true);
  }

  protected abrirEditar(p: Proyecto, ev: Event): void {
    ev.stopPropagation();
    this.editando.set(p);
    this.errorNombre.set(null);
    this.form.reset({ nombre: p.nombre, descripcion: p.descripcion ?? '' });
    this.modalAbierto.set(true);
  }

  protected abrir(p: Proyecto): void {
    void this.router.navigate(['/proyectos', p.id, 'sprints']);
  }

  protected async guardar(): Promise<void> {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    this.guardando.set(true);
    this.errorNombre.set(null);
    const req = this.form.getRawValue();
    try {
      const editando = this.editando();
      if (editando) {
        await this.service.actualizar(editando.id, req);
        this.toast.exito('Proyecto actualizado');
      } else {
        await this.service.crear(req);
        this.toast.exito('Proyecto creado');
      }
      this.modalAbierto.set(false);
      await this.service.cargar();
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

  protected async eliminar(p: Proyecto, ev: Event): Promise<void> {
    ev.stopPropagation();
    const ok = await this.confirm.ask({
      titulo: '¿Eliminar proyecto?',
      mensaje: `Se eliminará "${p.nombre}" junto con sus sprints, workitems y tareas. Esta acción no se puede deshacer.`,
      peligro: true,
      confirmar: 'Eliminar',
    });
    if (!ok) return;
    try {
      await this.service.eliminar(p.id);
      this.toast.exito('Proyecto eliminado');
      await this.service.cargar();
    } catch (err) {
      this.toast.error(mensajeDeError(err));
    }
  }
}
