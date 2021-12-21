import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { ICampaign } from '../campaign.model';
import { CampaignService } from '../service/campaign.service';

@Component({
  templateUrl: './campaign-delete-dialog.component.html',
})
export class CampaignDeleteDialogComponent {
  campaign?: ICampaign;

  constructor(protected campaignService: CampaignService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.campaignService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
