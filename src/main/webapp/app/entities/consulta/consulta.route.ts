import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Routes } from '@angular/router';
import { JhiResolvePagingParams } from 'ng-jhipster';
import { UserRouteAccessService } from 'app/core/auth/user-route-access-service';
import { Observable, of } from 'rxjs';
import { map } from 'rxjs/operators';
import { Consulta } from 'app/shared/model/consulta.model';
import { ConsultaService } from './consulta.service';
import { ConsultaComponent } from './consulta.component';
import { ConsultaDetailComponent } from './consulta-detail.component';
import { ConsultaUpdateComponent } from './consulta-update.component';
import { ConsultaDeletePopupComponent } from './consulta-delete-dialog.component';
import { IConsulta } from 'app/shared/model/consulta.model';
import { VideoComponent } from './video/video.component';

@Injectable({ providedIn: 'root' })
export class ConsultaResolve implements Resolve<IConsulta> {
  constructor(private service: ConsultaService) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IConsulta> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(map((consulta: HttpResponse<Consulta>) => consulta.body));
    }
    return of(new Consulta());
  }
}
export const consultaRoute: Routes = [
  {
    path: '',
    component: ConsultaComponent,
    resolve: {
      pagingParams: JhiResolvePagingParams
    },
    data: {
      authorities: ['ROLE_USER'],
      defaultSort: 'id,asc',
      pageTitle: 'Consultas'
    },
    canActivate: [UserRouteAccessService]
  },
  {
    path: ':id/view',
    component: ConsultaDetailComponent,
    resolve: {
      consulta: ConsultaResolve
    },
    data: {
      authorities: ['ROLE_USER'],
      pageTitle: 'Consultas'
    },
    canActivate: [UserRouteAccessService]
  },
  {
    path: ':id/video',
    component: VideoComponent,
    resolve: {
      consulta: ConsultaResolve
    },
    data: {
      authorities: ['ROLE_USER'],
      pageTitle: 'Consultas'
    },
    canActivate: [UserRouteAccessService]
  },
  {
    path: 'new',
    component: ConsultaUpdateComponent,
    resolve: {
      consulta: ConsultaResolve
    },
    data: {
      authorities: ['ROLE_USER'],
      pageTitle: 'Consultas'
    },
    canActivate: [UserRouteAccessService]
  },
  {
    path: ':id/edit',
    component: ConsultaUpdateComponent,
    resolve: {
      consulta: ConsultaResolve
    },
    data: {
      authorities: ['ROLE_USER'],
      pageTitle: 'Consultas'
    },
    canActivate: [UserRouteAccessService]
  }
];

export const consultaPopupRoute: Routes = [
  {
    path: ':id/delete',
    component: ConsultaDeletePopupComponent,
    resolve: {
      consulta: ConsultaResolve
    },
    data: {
      authorities: ['ROLE_USER'],
      pageTitle: 'Consultas'
    },
    canActivate: [UserRouteAccessService],
    outlet: 'popup'
  }
];
