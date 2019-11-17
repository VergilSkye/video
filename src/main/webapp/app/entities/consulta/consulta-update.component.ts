import { Component, OnInit } from '@angular/core';
// eslint-disable-next-line @typescript-eslint/no-unused-vars
import { HttpResponse, HttpErrorResponse } from '@angular/common/http';
// eslint-disable-next-line @typescript-eslint/no-unused-vars
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import * as moment from 'moment';
import { DATE_TIME_FORMAT } from 'app/shared/constants/input.constants';
import { JhiAlertService } from 'ng-jhipster';
import { IConsulta, Consulta } from 'app/shared/model/consulta.model';
import { ConsultaService } from './consulta.service';
import { IUser } from 'app/core/user/user.model';
import { UserService } from 'app/core/user/user.service';

@Component({
  selector: 'jhi-consulta-update',
  templateUrl: './consulta-update.component.html'
})
export class ConsultaUpdateComponent implements OnInit {
  isSaving: boolean;

  users: IUser[];

  editForm = this.fb.group({
    id: [],
    uuid: [],
    created_at: [],
    ended_at: [],
    duration: [],
    psicologo_idId: [],
    paciente_idId: []
  });

  constructor(
    protected jhiAlertService: JhiAlertService,
    protected consultaService: ConsultaService,
    protected userService: UserService,
    protected activatedRoute: ActivatedRoute,
    private fb: FormBuilder
  ) {}

  ngOnInit() {
    this.isSaving = false;
    this.activatedRoute.data.subscribe(({ consulta }) => {
      this.updateForm(consulta);
    });
    this.userService
      .query()
      .subscribe((res: HttpResponse<IUser[]>) => (this.users = res.body), (res: HttpErrorResponse) => this.onError(res.message));
  }

  updateForm(consulta: IConsulta) {
    this.editForm.patchValue({
      id: consulta.id,
      uuid: consulta.uuid,
      created_at: consulta.created_at != null ? consulta.created_at.format(DATE_TIME_FORMAT) : null,
      ended_at: consulta.ended_at != null ? consulta.ended_at.format(DATE_TIME_FORMAT) : null,
      duration: consulta.duration,
      psicologo_idId: consulta.psicologo_idId,
      paciente_idId: consulta.paciente_idId
    });
  }

  previousState() {
    window.history.back();
  }

  save() {
    this.isSaving = true;
    const consulta = this.createFromForm();
    if (consulta.id !== undefined) {
      this.subscribeToSaveResponse(this.consultaService.update(consulta));
    } else {
      this.subscribeToSaveResponse(this.consultaService.create(consulta));
    }
  }

  private createFromForm(): IConsulta {
    return {
      ...new Consulta(),
      id: this.editForm.get(['id']).value,
      uuid: this.editForm.get(['uuid']).value,
      created_at:
        this.editForm.get(['created_at']).value != null ? moment(this.editForm.get(['created_at']).value, DATE_TIME_FORMAT) : undefined,
      ended_at: this.editForm.get(['ended_at']).value != null ? moment(this.editForm.get(['ended_at']).value, DATE_TIME_FORMAT) : undefined,
      duration: this.editForm.get(['duration']).value,
      psicologo_idId: this.editForm.get(['psicologo_idId']).value,
      paciente_idId: this.editForm.get(['paciente_idId']).value
    };
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IConsulta>>) {
    result.subscribe(() => this.onSaveSuccess(), () => this.onSaveError());
  }

  protected onSaveSuccess() {
    this.isSaving = false;
    this.previousState();
  }

  protected onSaveError() {
    this.isSaving = false;
  }
  protected onError(errorMessage: string) {
    this.jhiAlertService.error(errorMessage, null, null);
  }

  trackUserById(index: number, item: IUser) {
    return item.id;
  }
}
