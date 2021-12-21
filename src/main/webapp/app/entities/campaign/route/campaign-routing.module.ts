import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { CampaignComponent } from '../list/campaign.component';
import { CampaignDetailComponent } from '../detail/campaign-detail.component';
import { CampaignUpdateComponent } from '../update/campaign-update.component';
import { CampaignRoutingResolveService } from './campaign-routing-resolve.service';

const campaignRoute: Routes = [
  {
    path: '',
    component: CampaignComponent,
    data: {
      defaultSort: 'id,asc',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: CampaignDetailComponent,
    resolve: {
      campaign: CampaignRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: CampaignUpdateComponent,
    resolve: {
      campaign: CampaignRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: CampaignUpdateComponent,
    resolve: {
      campaign: CampaignRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(campaignRoute)],
  exports: [RouterModule],
})
export class CampaignRoutingModule {}
