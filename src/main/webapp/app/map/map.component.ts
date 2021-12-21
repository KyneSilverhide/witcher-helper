import { Component, OnInit } from '@angular/core';
import * as L from 'leaflet';
import { LatLngBoundsExpression } from 'leaflet';
import { ICampaign } from '../entities/campaign/campaign.model';
import { CampaignService } from '../entities/campaign/service/campaign.service';
import { ScenarioService } from '../entities/scenario/service/scenario.service';
import { IScenario } from '../entities/scenario/scenario.model';

@Component({
  selector: 'jhi-map',
  templateUrl: './map.component.html',
  styleUrls: ['./map.component.scss'],
})
export class MapComponent implements OnInit {
  campaigns: ICampaign[] = [];
  selectedCampaign: ICampaign | null = null;
  scenarios: IScenario[] = [];

  constructor(private campaignService: CampaignService, private scenarioService: ScenarioService) {}

  ngOnInit(): void {
    this.campaignService.query({ size: 99 }).subscribe(value => (this.campaigns = value.body ?? []));
  }

  selectCampaign(campaign: ICampaign): void {
    this.selectedCampaign = campaign;
    this.scenarioService.query({ 'campaignId.equals': this.selectedCampaign.id }).subscribe(value => {
      if (value.body) {
        this.scenarios = value.body;
        this.initLeafletMap();
      }
    });
  }

  private initLeafletMap(): void {
    const map = L.map('map', {
      crs: L.CRS.Simple,
      minZoom: 1,
    });
    const bounds: LatLngBoundsExpression = [
      [0, 0],
      [1000, 1000],
    ];
    L.imageOverlay('../../content/images/witcher_continent_map.jpg', bounds).addTo(map);
    map.fitBounds(bounds);
  }
}
