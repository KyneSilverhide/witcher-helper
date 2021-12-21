import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { IScenario, Scenario } from '../scenario.model';
import { ScenarioService } from '../service/scenario.service';
import { AlertError } from 'app/shared/alert/alert-error.model';
import { EventManager, EventWithContent } from 'app/core/util/event-manager.service';
import { DataUtils, FileLoadError } from 'app/core/util/data-util.service';
import { ICampaign } from 'app/entities/campaign/campaign.model';
import { CampaignService } from 'app/entities/campaign/service/campaign.service';
import { IPlayer } from 'app/entities/player/player.model';
import { PlayerService } from 'app/entities/player/service/player.service';
import { rpgIcons } from '../../../config/font-awesome-icons';

@Component({
  selector: 'jhi-scenario-update',
  templateUrl: './scenario-update.component.html',
  styleUrls: ['./scenario-update.component.scss'],
})
export class ScenarioUpdateComponent implements OnInit {
  isSaving = false;

  rpgIcons = rpgIcons;
  campaignsSharedCollection: ICampaign[] = [];
  playersSharedCollection: IPlayer[] = [];

  editForm = this.fb.group({
    id: [],
    title: [null, [Validators.required]],
    description: [],
    mapCoords: [null, [Validators.required]],
    date: [],
    mapIcon: [null, [Validators.required]],
    campaign: [],
    players: [],
  });

  constructor(
    protected dataUtils: DataUtils,
    protected eventManager: EventManager,
    protected scenarioService: ScenarioService,
    protected campaignService: CampaignService,
    protected playerService: PlayerService,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ scenario }) => {
      this.updateForm(scenario);

      this.loadRelationshipsOptions();
    });
  }

  byteSize(base64String: string): string {
    return this.dataUtils.byteSize(base64String);
  }

  openFile(base64String: string, contentType: string | null | undefined): void {
    this.dataUtils.openFile(base64String, contentType);
  }

  setFileData(event: Event, field: string, isImage: boolean): void {
    this.dataUtils.loadFileToForm(event, this.editForm, field, isImage).subscribe({
      error: (err: FileLoadError) =>
        this.eventManager.broadcast(new EventWithContent<AlertError>('witcherhelperApp.error', { ...err, key: 'error.file.' + err.key })),
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const scenario = this.createFromForm();
    if (scenario.id !== undefined) {
      this.subscribeToSaveResponse(this.scenarioService.update(scenario));
    } else {
      this.subscribeToSaveResponse(this.scenarioService.create(scenario));
    }
  }

  trackCampaignById(index: number, item: ICampaign): number {
    return item.id!;
  }

  trackPlayerById(index: number, item: IPlayer): number {
    return item.id!;
  }

  getSelectedPlayer(option: IPlayer, selectedVals?: IPlayer[]): IPlayer {
    if (selectedVals) {
      for (const selectedVal of selectedVals) {
        if (option.id === selectedVal.id) {
          return selectedVal;
        }
      }
    }
    return option;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IScenario>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe(
      () => this.onSaveSuccess(),
      () => this.onSaveError()
    );
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(scenario: IScenario): void {
    this.editForm.patchValue({
      id: scenario.id,
      title: scenario.title,
      description: scenario.description,
      mapCoords: scenario.mapCoords,
      date: scenario.date,
      mapIcon: scenario.mapIcon,
      campaign: scenario.campaign,
      players: scenario.players,
    });

    this.campaignsSharedCollection = this.campaignService.addCampaignToCollectionIfMissing(
      this.campaignsSharedCollection,
      scenario.campaign
    );
    this.playersSharedCollection = this.playerService.addPlayerToCollectionIfMissing(
      this.playersSharedCollection,
      ...(scenario.players ?? [])
    );
  }

  protected loadRelationshipsOptions(): void {
    this.campaignService
      .query()
      .pipe(map((res: HttpResponse<ICampaign[]>) => res.body ?? []))
      .pipe(
        map((campaigns: ICampaign[]) =>
          this.campaignService.addCampaignToCollectionIfMissing(campaigns, this.editForm.get('campaign')!.value)
        )
      )
      .subscribe((campaigns: ICampaign[]) => (this.campaignsSharedCollection = campaigns));

    this.playerService
      .query()
      .pipe(map((res: HttpResponse<IPlayer[]>) => res.body ?? []))
      .pipe(
        map((players: IPlayer[]) =>
          this.playerService.addPlayerToCollectionIfMissing(players, ...(this.editForm.get('players')!.value ?? []))
        )
      )
      .subscribe((players: IPlayer[]) => (this.playersSharedCollection = players));
  }

  protected createFromForm(): IScenario {
    return {
      ...new Scenario(),
      id: this.editForm.get(['id'])!.value,
      title: this.editForm.get(['title'])!.value,
      description: this.editForm.get(['description'])!.value,
      mapCoords: this.editForm.get(['mapCoords'])!.value,
      date: this.editForm.get(['date'])!.value,
      mapIcon: this.editForm.get(['mapIcon'])!.value,
      campaign: this.editForm.get(['campaign'])!.value,
      players: this.editForm.get(['players'])!.value,
    };
  }
}
