import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

import { TestSharedModule } from 'app/shared/shared.module';
import { ConsultaComponent } from './consulta.component';
import { ConsultaDetailComponent } from './consulta-detail.component';
import { ConsultaUpdateComponent } from './consulta-update.component';
import { ConsultaDeletePopupComponent, ConsultaDeleteDialogComponent } from './consulta-delete-dialog.component';
import { consultaRoute, consultaPopupRoute } from './consulta.route';
import { VideoComponent } from './video/video.component';
import { UserVideoComponent } from './video/user-video.component';
import { OpenViduVideoComponent } from './video/ov-video.component';

const ENTITY_STATES = [...consultaRoute, ...consultaPopupRoute];

@NgModule({
  imports: [TestSharedModule, RouterModule.forChild(ENTITY_STATES)],
  declarations: [
    VideoComponent,
    UserVideoComponent,
    OpenViduVideoComponent,
    ConsultaComponent,
    ConsultaDetailComponent,
    ConsultaUpdateComponent,
    ConsultaDeleteDialogComponent,
    ConsultaDeletePopupComponent
  ],
  entryComponents: [ConsultaDeleteDialogComponent]
})
export class TestConsultaModule {}
