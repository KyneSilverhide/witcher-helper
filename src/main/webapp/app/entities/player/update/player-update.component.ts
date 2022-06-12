import { Component, OnInit, ElementRef } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { IPlayer, Player } from '../player.model';
import { PlayerService } from '../service/player.service';
import { AlertError } from 'app/shared/alert/alert-error.model';
import { EventManager, EventWithContent } from 'app/core/util/event-manager.service';
import { DataUtils, FileLoadError } from 'app/core/util/data-util.service';
import { ICampaign } from 'app/entities/campaign/campaign.model';
import { CampaignService } from 'app/entities/campaign/service/campaign.service';
import { Race } from 'app/entities/enumerations/race.model';
import { Profession } from 'app/entities/enumerations/profession.model';

@Component({
  selector: 'jhi-player-update',
  templateUrl: './player-update.component.html',
})
export class PlayerUpdateComponent implements OnInit {
  isSaving = false;
  raceValues = Object.keys(Race);
  professionValues = Object.keys(Profession);

  campaignsSharedCollection: ICampaign[] = [];

  editForm = this.fb.group({
    id: [],
    name: [null, [Validators.required]],
    race: [null, [Validators.required]],
    profession: [null, [Validators.required]],
    picture: [],
    pictureContentType: [],
    description: [],
    campaign: [],
  });

  constructor(
    protected dataUtils: DataUtils,
    protected eventManager: EventManager,
    protected playerService: PlayerService,
    protected campaignService: CampaignService,
    protected elementRef: ElementRef,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ player }) => {
      this.updateForm(player);

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
        this.eventManager.broadcast(new EventWithContent<AlertError>('jdrhelperApp.error', { ...err, key: 'error.file.' + err.key })),
    });
  }

  clearInputImage(field: string, fieldContentType: string, idInput: string): void {
    this.editForm.patchValue({
      [field]: null,
      [fieldContentType]: null,
    });
    if (idInput && this.elementRef.nativeElement.querySelector('#' + idInput)) {
      this.elementRef.nativeElement.querySelector('#' + idInput).value = null;
    }
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const player = this.createFromForm();
    if (player.id !== undefined) {
      this.subscribeToSaveResponse(this.playerService.update(player));
    } else {
      this.subscribeToSaveResponse(this.playerService.create(player));
    }
  }

  trackCampaignById(index: number, item: ICampaign): number {
    return item.id!;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IPlayer>>): void {
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

  protected updateForm(player: IPlayer): void {
    this.editForm.patchValue({
      id: player.id,
      name: player.name,
      race: player.race,
      profession: player.profession,
      picture: player.picture,
      pictureContentType: player.pictureContentType,
      description: player.description,
      campaign: player.campaign,
    });

    this.campaignsSharedCollection = this.campaignService.addCampaignToCollectionIfMissing(this.campaignsSharedCollection, player.campaign);
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
  }

  protected createFromForm(): IPlayer {
    return {
      ...new Player(),
      id: this.editForm.get(['id'])!.value,
      name: this.editForm.get(['name'])!.value,
      race: this.editForm.get(['race'])!.value,
      profession: this.editForm.get(['profession'])!.value,
      pictureContentType: this.editForm.get(['pictureContentType'])!.value,
      picture: this.editForm.get(['picture'])!.value,
      description: this.editForm.get(['description'])!.value,
      campaign: this.editForm.get(['campaign'])!.value,
    };
  }
}
