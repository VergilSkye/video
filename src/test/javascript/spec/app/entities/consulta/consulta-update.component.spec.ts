import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { of } from 'rxjs';

import { TestTestModule } from '../../../test.module';
import { ConsultaUpdateComponent } from 'app/entities/consulta/consulta-update.component';
import { ConsultaService } from 'app/entities/consulta/consulta.service';
import { Consulta } from 'app/shared/model/consulta.model';

describe('Component Tests', () => {
  describe('Consulta Management Update Component', () => {
    let comp: ConsultaUpdateComponent;
    let fixture: ComponentFixture<ConsultaUpdateComponent>;
    let service: ConsultaService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [TestTestModule],
        declarations: [ConsultaUpdateComponent],
        providers: [FormBuilder]
      })
        .overrideTemplate(ConsultaUpdateComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(ConsultaUpdateComponent);
      comp = fixture.componentInstance;
      service = fixture.debugElement.injector.get(ConsultaService);
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', fakeAsync(() => {
        // GIVEN
        const entity = new Consulta(123);
        spyOn(service, 'update').and.returnValue(of(new HttpResponse({ body: entity })));
        comp.updateForm(entity);
        // WHEN
        comp.save();
        tick(); // simulate async

        // THEN
        expect(service.update).toHaveBeenCalledWith(entity);
        expect(comp.isSaving).toEqual(false);
      }));

      it('Should call create service on save for new entity', fakeAsync(() => {
        // GIVEN
        const entity = new Consulta();
        spyOn(service, 'create').and.returnValue(of(new HttpResponse({ body: entity })));
        comp.updateForm(entity);
        // WHEN
        comp.save();
        tick(); // simulate async

        // THEN
        expect(service.create).toHaveBeenCalledWith(entity);
        expect(comp.isSaving).toEqual(false);
      }));
    });
  });
});
