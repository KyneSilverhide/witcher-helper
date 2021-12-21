import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { ICampaign, getCampaignIdentifier } from '../campaign.model';

export type EntityResponseType = HttpResponse<ICampaign>;
export type EntityArrayResponseType = HttpResponse<ICampaign[]>;

@Injectable({ providedIn: 'root' })
export class CampaignService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/campaigns');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(campaign: ICampaign): Observable<EntityResponseType> {
    return this.http.post<ICampaign>(this.resourceUrl, campaign, { observe: 'response' });
  }

  update(campaign: ICampaign): Observable<EntityResponseType> {
    return this.http.put<ICampaign>(`${this.resourceUrl}/${getCampaignIdentifier(campaign) as number}`, campaign, { observe: 'response' });
  }

  partialUpdate(campaign: ICampaign): Observable<EntityResponseType> {
    return this.http.patch<ICampaign>(`${this.resourceUrl}/${getCampaignIdentifier(campaign) as number}`, campaign, {
      observe: 'response',
    });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<ICampaign>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<ICampaign[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addCampaignToCollectionIfMissing(campaignCollection: ICampaign[], ...campaignsToCheck: (ICampaign | null | undefined)[]): ICampaign[] {
    const campaigns: ICampaign[] = campaignsToCheck.filter(isPresent);
    if (campaigns.length > 0) {
      const campaignCollectionIdentifiers = campaignCollection.map(campaignItem => getCampaignIdentifier(campaignItem)!);
      const campaignsToAdd = campaigns.filter(campaignItem => {
        const campaignIdentifier = getCampaignIdentifier(campaignItem);
        if (campaignIdentifier == null || campaignCollectionIdentifiers.includes(campaignIdentifier)) {
          return false;
        }
        campaignCollectionIdentifiers.push(campaignIdentifier);
        return true;
      });
      return [...campaignsToAdd, ...campaignCollection];
    }
    return campaignCollection;
  }
}
