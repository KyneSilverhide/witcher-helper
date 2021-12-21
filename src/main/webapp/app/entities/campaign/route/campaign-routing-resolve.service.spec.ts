jest.mock('@angular/router');

import { TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { of } from 'rxjs';

import { ICampaign, Campaign } from '../campaign.model';
import { CampaignService } from '../service/campaign.service';

import { CampaignRoutingResolveService } from './campaign-routing-resolve.service';

describe('Campaign routing resolve service', () => {
  let mockRouter: Router;
  let mockActivatedRouteSnapshot: ActivatedRouteSnapshot;
  let routingResolveService: CampaignRoutingResolveService;
  let service: CampaignService;
  let resultCampaign: ICampaign | undefined;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [Router, ActivatedRouteSnapshot],
    });
    mockRouter = TestBed.inject(Router);
    mockActivatedRouteSnapshot = TestBed.inject(ActivatedRouteSnapshot);
    routingResolveService = TestBed.inject(CampaignRoutingResolveService);
    service = TestBed.inject(CampaignService);
    resultCampaign = undefined;
  });

  describe('resolve', () => {
    it('should return ICampaign returned by find', () => {
      // GIVEN
      service.find = jest.fn(id => of(new HttpResponse({ body: { id } })));
      mockActivatedRouteSnapshot.params = { id: 123 };

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultCampaign = result;
      });

      // THEN
      expect(service.find).toBeCalledWith(123);
      expect(resultCampaign).toEqual({ id: 123 });
    });

    it('should return new ICampaign if id is not provided', () => {
      // GIVEN
      service.find = jest.fn();
      mockActivatedRouteSnapshot.params = {};

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultCampaign = result;
      });

      // THEN
      expect(service.find).not.toBeCalled();
      expect(resultCampaign).toEqual(new Campaign());
    });

    it('should route to 404 page if data not found in server', () => {
      // GIVEN
      jest.spyOn(service, 'find').mockReturnValue(of(new HttpResponse({ body: null as unknown as Campaign })));
      mockActivatedRouteSnapshot.params = { id: 123 };

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultCampaign = result;
      });

      // THEN
      expect(service.find).toBeCalledWith(123);
      expect(resultCampaign).toEqual(undefined);
      expect(mockRouter.navigate).toHaveBeenCalledWith(['404']);
    });
  });
});
