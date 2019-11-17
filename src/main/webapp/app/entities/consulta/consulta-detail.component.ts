import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IConsulta } from 'app/shared/model/consulta.model';

@Component({
  selector: 'jhi-consulta-detail',
  templateUrl: './consulta-detail.component.html'
})
export class ConsultaDetailComponent implements OnInit {
  consulta: IConsulta;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit() {
    this.activatedRoute.data.subscribe(({ consulta }) => {
      this.consulta = consulta;
    });
  }

  previousState() {
    window.history.back();
  }
}
