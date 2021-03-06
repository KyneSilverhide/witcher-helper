import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import * as dayjs from 'dayjs';

import { DATE_FORMAT } from 'app/config/input.constants';
import { IScenario, Scenario } from '../scenario.model';

import { ScenarioService } from './scenario.service';

describe('Scenario Service', () => {
  let service: ScenarioService;
  let httpMock: HttpTestingController;
  let elemDefault: IScenario;
  let expectedResult: IScenario | IScenario[] | boolean | null;
  let currentDate: dayjs.Dayjs;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(ScenarioService);
    httpMock = TestBed.inject(HttpTestingController);
    currentDate = dayjs();

    elemDefault = {
      id: 0,
      title: 'AAAAAAA',
      description: 'AAAAAAA',
      mapCoords: 'AAAAAAA',
      date: currentDate,
      mapIcon: 'AAAAAAA',
    };
  });

  describe('Service methods', () => {
    it('should find an element', () => {
      const returnedFromService = Object.assign(
        {
          date: currentDate.format(DATE_FORMAT),
        },
        elemDefault
      );

      service.find(123).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(elemDefault);
    });

    it('should create a Scenario', () => {
      const returnedFromService = Object.assign(
        {
          id: 0,
          date: currentDate.format(DATE_FORMAT),
        },
        elemDefault
      );

      const expected = Object.assign(
        {
          date: currentDate,
        },
        returnedFromService
      );

      service.create(new Scenario()).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Scenario', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          title: 'BBBBBB',
          description: 'BBBBBB',
          mapCoords: 'BBBBBB',
          date: currentDate.format(DATE_FORMAT),
          mapIcon: 'BBBBBB',
        },
        elemDefault
      );

      const expected = Object.assign(
        {
          date: currentDate,
        },
        returnedFromService
      );

      service.update(expected).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Scenario', () => {
      const patchObject = Object.assign(
        {
          description: 'BBBBBB',
          mapCoords: 'BBBBBB',
          mapIcon: 'BBBBBB',
        },
        new Scenario()
      );

      const returnedFromService = Object.assign(patchObject, elemDefault);

      const expected = Object.assign(
        {
          date: currentDate,
        },
        returnedFromService
      );

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Scenario', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          title: 'BBBBBB',
          description: 'BBBBBB',
          mapCoords: 'BBBBBB',
          date: currentDate.format(DATE_FORMAT),
          mapIcon: 'BBBBBB',
        },
        elemDefault
      );

      const expected = Object.assign(
        {
          date: currentDate,
        },
        returnedFromService
      );

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toContainEqual(expected);
    });

    it('should delete a Scenario', () => {
      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addScenarioToCollectionIfMissing', () => {
      it('should add a Scenario to an empty array', () => {
        const scenario: IScenario = { id: 123 };
        expectedResult = service.addScenarioToCollectionIfMissing([], scenario);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(scenario);
      });

      it('should not add a Scenario to an array that contains it', () => {
        const scenario: IScenario = { id: 123 };
        const scenarioCollection: IScenario[] = [
          {
            ...scenario,
          },
          { id: 456 },
        ];
        expectedResult = service.addScenarioToCollectionIfMissing(scenarioCollection, scenario);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Scenario to an array that doesn't contain it", () => {
        const scenario: IScenario = { id: 123 };
        const scenarioCollection: IScenario[] = [{ id: 456 }];
        expectedResult = service.addScenarioToCollectionIfMissing(scenarioCollection, scenario);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(scenario);
      });

      it('should add only unique Scenario to an array', () => {
        const scenarioArray: IScenario[] = [{ id: 123 }, { id: 456 }, { id: 64698 }];
        const scenarioCollection: IScenario[] = [{ id: 123 }];
        expectedResult = service.addScenarioToCollectionIfMissing(scenarioCollection, ...scenarioArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const scenario: IScenario = { id: 123 };
        const scenario2: IScenario = { id: 456 };
        expectedResult = service.addScenarioToCollectionIfMissing([], scenario, scenario2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(scenario);
        expect(expectedResult).toContain(scenario2);
      });

      it('should accept null and undefined values', () => {
        const scenario: IScenario = { id: 123 };
        expectedResult = service.addScenarioToCollectionIfMissing([], null, scenario, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(scenario);
      });

      it('should return initial array if no Scenario is added', () => {
        const scenarioCollection: IScenario[] = [{ id: 123 }];
        expectedResult = service.addScenarioToCollectionIfMissing(scenarioCollection, undefined, null);
        expect(expectedResult).toEqual(scenarioCollection);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
