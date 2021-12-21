import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { ICampaign, Campaign } from '../campaign.model';
import { CampaignService } from '../service/campaign.service';

@Injectable({ providedIn: 'root' })
export class CampaignRoutingResolveService implements Resolve<ICampaign> {
  constructor(protected service: CampaignService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<ICampaign> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((campaign: HttpResponse<Campaign>) => {
          if (campaign.body) {
            return of(campaign.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new Campaign());
  }
}
