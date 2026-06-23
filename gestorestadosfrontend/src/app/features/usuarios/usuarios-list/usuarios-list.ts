import { HttpErrorResponse } from '@angular/common/http';
import { ChangeDetectionStrategy, Component, inject, OnInit, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { erroresDeCampo, mensajeDeError } from '../../../core/http/problem-detail.util';
import { Usuario } from '../../../core/models';
import { ShellService } from '../../../layout/shell.service';
import {
  ButtonDirective,
  ConfirmService,
  EmptyState,
  FormField,
  Modal,
  Spinner,
  Table,
  ToastService,
} from '../../../shared/ui';
import { UsuarioService } from '../data-access/usuario.service';

@Component({
  selector: 'app-usuarios-list',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [ReactiveFormsModule, ButtonDirective, EmptyState, Spinner, Modal, FormField, Table],
  templateUrl: './usuarios-list.html',
})
export class UsuariosList implements OnInit {
  protected readonly service = inject(UsuarioService);
  private readonly shell = inject(ShellService);
  private readonly toast = inject(ToastService);
  private readonly confirmService = inject(ConfirmService);
  private readonly fb = inject(FormBuilder);

  protected readonly modalAbierto = signal(false);
  protected readonly editando = signal<Usuario | null>(null);
  protected readonly guardando = signal(false);
  protected readonly errorNombre = signal<string | null>(null);
  protected readonly errorEmail = signal<string | null>(null);

  protected readonly form = this.fb.nonNullable.group({
    nombre: ['', [Validators.required, Validators.maxLength(255)]],
    email: ['', [Validators.required, Validators.email]],
    activo: [true],
  });

  ngOnInit(): void {
    this.shell.set('Usuarios', [{ label: 'Usuarios' }]);
    void this.service.cargar();
  }

  protected abrirNuevo(): void {
    this.editando.set(null);
    this.limpiarErrores();
    this.form.reset({ nombre: '', email: '', activo: true });
    this.modalAbierto.set(true);
  }

  protected abrirEditar(u: Usuario): void {
    this.editando.set(u);
    this.limpiarErrores();
    this.form.reset({ nombre: u.nombre, email: u.email, activo: u.activo });
    this.modalAbierto.set(true);
  }

  protected async guardar(): Promise<void> {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    this.guardando.set(true);
    this.limpiarErrores();
    const req = this.form.getRawValue();
    try {
      const editando = this.editando();
      if (editando) {
        await this.service.actualizar(editando.id, req);
        this.toast.exito('Usuario actualizado');
      } else {
        await this.service.crear(req);
        this.toast.exito('Usuario creado');
      }
      this.modalAbierto.set(false);
      await this.service.cargar();
    } catch (err) {
      this.manejarError(err);
    } finally {
      this.guardando.set(false);
    }
  }

  protected async eliminar(u: Usuario): Promise<void> {
    const ok = await this.confirmService.ask({
      titulo: '¿Eliminar usuario?',
      mensaje: `Se eliminará "${u.nombre}". Sus asignaciones existentes quedarán sin usuario. Esta acción no se puede deshacer.`,
      peligro: true,
      confirmar: 'Eliminar',
    });
    if (!ok) return;
    try {
      await this.service.eliminar(u.id);
      this.toast.exito('Usuario eliminado');
      await this.service.cargar();
    } catch (err) {
      this.toast.error(mensajeDeError(err));
    }
  }

  private manejarError(err: unknown): void {
    // Email duplicado -> 409
    if (err instanceof HttpErrorResponse && err.status === 409) {
      this.errorEmail.set(mensajeDeError(err));
      return;
    }
    const campos = erroresDeCampo(err);
    if (campos?.['nombre']) this.errorNombre.set(campos['nombre']);
    if (campos?.['email']) this.errorEmail.set(campos['email']);
    if (!campos) this.toast.error(mensajeDeError(err));
  }

  private limpiarErrores(): void {
    this.errorNombre.set(null);
    this.errorEmail.set(null);
  }
}
