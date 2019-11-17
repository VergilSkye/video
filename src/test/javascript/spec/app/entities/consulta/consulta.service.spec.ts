import { TestBed, getTestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { take, map } from 'rxjs/operators';
import * as moment from 'moment';
import { DATE_TIME_FORMAT } from 'app/shared/constants/input.constants';
import { ConsultaService } from 'app/entities/consulta/consulta.service';
import { IConsulta, Consulta } from 'app/shared/model/consulta.model';

describe('Service Tests', () => {
  describe('Consulta Service', () => {
    let injector: TestBed;
    let service: ConsultaService;
    let httpMock: HttpTestingController;
    let elemDefault: IConsulta;
    let expectedResult;
    let currentDate: moment.Moment;
    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule]
      });
      expectedResult = {};
      injector = getTestBed();
      service = injector.get(ConsultaService);
      httpMock = injector.get(HttpTestingController);
      currentDate = moment();

      elemDefault = new Consulta(0, 'AAAAAAA', currentDate, currentDate, 0);
    });

    describe('Service methods', () => {
      it('should find an element', () => {
        const returnedFromService = Object.assign(
          {
            created_at: currentDate.format(DATE_TIME_FORMAT),
            ended_at: currentDate.format(DATE_TIME_FORMAT)
          },
          elemDefault
        );
        service
          .find(123)
          .pipe(take(1))
          .subscribe(resp => (expectedResult = resp));

        const req = httpMock.expectOne({ method: 'GET' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject({ body: elemDefault });
      });

      it('should create a Consulta', () => {
        const returnedFromService = Object.assign(
          {
            id: 0,
            created_at: currentDate.format(DATE_TIME_FORMAT),
            ended_at: currentDate.format(DATE_TIME_FORMAT)
          },
          elemDefault
        );
        const expected = Object.assign(
          {
            created_at: currentDate,
            ended_at: currentDate
          },
          returnedFromService
        );
        service
          .create(new Consulta(null))
          .pipe(take(1))
          .subscribe(resp => (expectedResult = resp));
        const req = httpMock.expectOne({ method: 'POST' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject({ body: expected });
      });

      it('should update a Consulta', () => {
        const returnedFromService = Object.assign(
          {
            uuid: 'BBBBBB',
            created_at: currentDate.format(DATE_TIME_FORMAT),
            ended_at: currentDate.format(DATE_TIME_FORMAT),
            duration: 'BBBBBB'
          },
          elemDefault
        );

        const expected = Object.assign(
          {
            created_at: currentDate,
            ended_at: currentDate
          },
          returnedFromService
        );
        service
          .update(expected)
          .pipe(take(1))
          .subscribe(resp => (expectedResult = resp));
        const req = httpMock.expectOne({ method: 'PUT' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject({ body: expected });
      });

      it('should return a list of Consulta', () => {
        const returnedFromService = Object.assign(
          {
            uuid: 'BBBBBB',
            created_at: currentDate.format(DATE_TIME_FORMAT),
            ended_at: currentDate.format(DATE_TIME_FORMAT),
            duration: 'BBBBBB'
          },
          elemDefault
        );
        const expected = Object.assign(
          {
            created_at: currentDate,
            ended_at: currentDate
          },
          returnedFromService
        );
        service
          .query(expected)
          .pipe(
            take(1),
            map(resp => resp.body)
          )
          .subscribe(body => (expectedResult = body));
        const req = httpMock.expectOne({ method: 'GET' });
        req.flush([returnedFromService]);
        httpMock.verify();
        expect(expectedResult).toContainEqual(expected);
      });

      it('should delete a Consulta', () => {
        service.delete(123).subscribe(resp => (expectedResult = resp.ok));

        const req = httpMock.expectOne({ method: 'DELETE' });
        req.flush({ status: 200 });
        expect(expectedResult);
      });
    });

    afterEach(() => {
      httpMock.verify();
    });
  });
});
