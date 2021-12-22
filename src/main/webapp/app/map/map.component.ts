import { Component, OnInit } from '@angular/core';
import * as L from 'leaflet';
import { icon, Icon, LatLng, LatLngBoundsExpression, LeafletMouseEvent, Map, Marker } from 'leaflet';
import { ICampaign } from '../entities/campaign/campaign.model';
import { CampaignService } from '../entities/campaign/service/campaign.service';
import { ScenarioService } from '../entities/scenario/service/scenario.service';
import { IScenario, Scenario } from '../entities/scenario/scenario.model';
import { IPlayer } from '../entities/player/player.model';
import { PlayerService } from '../entities/player/service/player.service';
import { MessageService } from 'primeng/api';
import { TranslateService } from '@ngx-translate/core';

@Component({
  selector: 'jhi-map',
  templateUrl: './map.component.html',
  styleUrls: ['./map.component.scss'],
})
export class MapComponent implements OnInit {
  campaigns: ICampaign[] = [];
  selectedCampaign: ICampaign | null = null;
  scenarios: IScenario[] = [];
  showEventPopup = false;
  scenario: IScenario = new Scenario();
  map: Map | null = null;
  lastMarker: Marker | null = null;
  players: IPlayer[] = [];

  constructor(
    private campaignService: CampaignService,
    private scenarioService: ScenarioService,
    private playerService: PlayerService,
    private messageService: MessageService,
    private translateService: TranslateService
  ) {}

  ngOnInit(): void {
    this.campaignService.query({ size: 99 }).subscribe(value => {
      this.campaigns = value.body ?? [];
      if (this.campaigns.length === 1) {
        this.selectCampaign(this.campaigns[0]);
      }
    });
  }

  selectCampaign(campaign: ICampaign): void {
    this.players = [];
    this.selectedCampaign = campaign;
    this.messageService.add({ severity: 'info', summary: 'Info', detail: this.translateService.instant('map.clickToAdd') });
    this.playerService.query({ 'campaignId.equals': this.selectedCampaign.id }).subscribe(value => (this.players = value.body ?? []));
    this.scenario.campaign = this.selectedCampaign;
    this.map = this.initLeafletMap();
    this.loadScenarios();
  }

  saveEvent($event: IScenario): void {
    this.scenarioService.create($event).subscribe(() => {
      this.cancelEvent();
      this.loadScenarios();
    });
  }

  cancelEvent(): void {
    this.showEventPopup = false;
    this.lastMarker?.remove();
  }

  private loadScenarios(): void {
    this.scenarioService.query({ 'campaignId.equals': this.selectedCampaign!.id }).subscribe(value => {
      if (value.body) {
        this.scenarios = value.body;
        let previousMarker: Marker | null = null;
        this.scenarios.forEach(scenario => {
          if (scenario.mapCoords && this.map) {
            const latlng = scenario.mapCoords.split(',');

            const customMarker: Icon = icon({
              iconUrl: '../../content/images/markers/' + scenario.mapIcon! + '.png',
              className: 'custommarker',
              iconSize: [40, 40],
              iconAnchor: [20, 40],
            });

            const newMarker = L.marker(new LatLng(Number(latlng[0]), Number(latlng[1])), { icon: customMarker })
              .addTo(this.map)
              .bindPopup(scenario.title!);
            if (previousMarker != null) {
              L.polyline([previousMarker.getLatLng(), newMarker.getLatLng()], { color: 'black' }).addTo(this.map);
            }
            previousMarker = newMarker;
          }
        });
      }
    });
  }

  private initLeafletMap(): Map {
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

    map.on('click', this.showNewEventPopup.bind(this));
    return map;
  }

  private showNewEventPopup(e: LeafletMouseEvent): void {
    if (!this.showEventPopup) {
      this.lastMarker = L.marker(e.latlng).addTo(this.map!);
      this.scenario = new Scenario();
      this.scenario.campaign = this.selectedCampaign;
      this.scenario.mapCoords = String(e.latlng.lat) + ',' + String(e.latlng.lng);
      this.showEventPopup = true;
    }
  }
}
