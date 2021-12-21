import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { ICampaign } from '../campaign.model';

@Component({
  selector: 'jhi-campaign-detail',
  templateUrl: './campaign-detail.component.html',
})
export class CampaignDetailComponent implements OnInit {
  campaign: ICampaign | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ campaign }) => {
      this.campaign = campaign;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
