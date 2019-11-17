import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { NgbActiveModal, NgbModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { JhiEventManager } from 'ng-jhipster';

import { IConsulta } from 'app/shared/model/consulta.model';
import { ConsultaService } from './consulta.service';

@Component({
  selector: 'jhi-consulta-delete-dialog',
  templateUrl: './consulta-delete-dialog.component.html'
})
export class ConsultaDeleteDialogComponent {
  consulta: IConsulta;

  constructor(protected consultaService: ConsultaService, public activeModal: NgbActiveModal, protected eventManager: JhiEventManager) {}

  clear() {
    this.activeModal.dismiss('cancel');
  }

  confirmDelete(id: number) {
    this.consultaService.delete(id).subscribe(() => {
      this.eventManager.broadcast({
        name: 'consultaListModification',
        content: 'Deleted an consulta'
      });
      this.activeModal.dismiss(true);
    });
  }
}

@Component({
  selector: 'jhi-consulta-delete-popup',
  template: ''
})
export class ConsultaDeletePopupComponent implements OnInit, OnDestroy {
  protected ngbModalRef: NgbModalRef;

  constructor(protected activatedRoute: ActivatedRoute, protected router: Router, protected modalService: NgbModal) {}

  ngOnInit() {
    this.activatedRoute.data.subscribe(({ consulta }) => {
      setTimeout(() => {
        this.ngbModalRef = this.modalService.open(ConsultaDeleteDialogComponent as Component, { size: 'lg', backdrop: 'static' });
        this.ngbModalRef.componentInstance.consulta = consulta;
        this.ngbModalRef.result.then(
          () => {
            this.router.navigate(['/consulta', { outlets: { popup: null } }]);
            this.ngbModalRef = null;
          },
          () => {
            this.router.navigate(['/consulta', { outlets: { popup: null } }]);
            this.ngbModalRef = null;
          }
        );
      }, 0);
    });
  }

  ngOnDestroy() {
    this.ngbModalRef = null;
  }
}
