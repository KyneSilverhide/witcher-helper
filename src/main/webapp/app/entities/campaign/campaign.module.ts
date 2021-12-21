import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { CampaignComponent } from './list/campaign.component';
import { CampaignDetailComponent } from './detail/campaign-detail.component';
import { CampaignUpdateComponent } from './update/campaign-update.component';
import { CampaignDeleteDialogComponent } from './delete/campaign-delete-dialog.component';
import { CampaignRoutingModule } from './route/campaign-routing.module';

@NgModule({
  imports: [SharedModule, CampaignRoutingModule],
  declarations: [CampaignComponent, CampaignDetailComponent, CampaignUpdateComponent, CampaignDeleteDialogComponent],
  entryComponents: [CampaignDeleteDialogComponent],
})
export class CampaignModule {}
