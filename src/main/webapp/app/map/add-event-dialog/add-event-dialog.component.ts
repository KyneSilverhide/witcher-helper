import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { PlayerService } from '../../entities/player/service/player.service';
import { IPlayer } from '../../entities/player/player.model';
import { rpgIcons } from 'app/config/font-awesome-icons';
import { map } from 'rxjs/operators';
import { HttpResponse } from '@angular/common/http';
import { IScenario, Scenario } from '../../entities/scenario/scenario.model';
import { FormBuilder, Validators } from '@angular/forms';

@Component({
  selector: 'jhi-add-event-dialog',
  templateUrl: './add-event-dialog.component.html',
  styleUrls: ['./add-event-dialog.component.scss'],
})
export class AddEventDialogComponent implements OnInit {
  rpgIcons = rpgIcons;
  playersSharedCollection: IPlayer[] = [];
  @Input() scenario: IScenario = new Scenario();
  @Input() visible = false;
  @Output() onSavedEvent = new EventEmitter<IScenario>();
  @Output() onClosed = new EventEmitter<void>();

  editForm = this.fb.group({
    title: [null, [Validators.required]],
    description: [],
    date: [null, [Validators.required]],
    mapIcon: [null, [Validators.required]],
    players: [],
  });

  constructor(private playerService: PlayerService, private fb: FormBuilder) {
    /**/
  }

  ngOnInit(): void {
    if (this.scenario.campaign?.id) {
      this.playerService
        .query({ 'campaignId.equals': this.scenario.campaign.id })
        .pipe(map((res: HttpResponse<IPlayer[]>) => res.body ?? []))
        .pipe(map((players: IPlayer[]) => this.playerService.addPlayerToCollectionIfMissing(players, ...(this.scenario.players ?? []))))
        .subscribe((players: IPlayer[]) => (this.playersSharedCollection = players));
    }
  }

  saveEvent(): void {
    const newScenario = {
      ...new Scenario(),
      title: this.editForm.get(['title'])!.value,
      description: this.editForm.get(['description'])!.value,
      mapCoords: this.scenario.mapCoords,
      date: this.editForm.get(['date'])!.value,
      mapIcon: this.editForm.get(['mapIcon'])!.value,
      campaign: this.scenario.campaign,
      players: this.editForm.get(['players'])!.value,
    };
    this.onSavedEvent.emit(newScenario);
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

  closePopup(): void {
    this.onClosed.emit();
  }
}
