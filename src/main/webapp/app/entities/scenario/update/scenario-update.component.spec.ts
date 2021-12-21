jest.mock('@angular/router');

import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { of, Subject } from 'rxjs';

import { ScenarioService } from '../service/scenario.service';
import { IScenario, Scenario } from '../scenario.model';
import { ICampaign } from 'app/entities/campaign/campaign.model';
import { CampaignService } from 'app/entities/campaign/service/campaign.service';
import { IPlayer } from 'app/entities/player/player.model';
import { PlayerService } from 'app/entities/player/service/player.service';

import { ScenarioUpdateComponent } from './scenario-update.component';

describe('Scenario Management Update Component', () => {
  let comp: ScenarioUpdateComponent;
  let fixture: ComponentFixture<ScenarioUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let scenarioService: ScenarioService;
  let campaignService: CampaignService;
  let playerService: PlayerService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      declarations: [ScenarioUpdateComponent],
      providers: [FormBuilder, ActivatedRoute],
    })
      .overrideTemplate(ScenarioUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(ScenarioUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    scenarioService = TestBed.inject(ScenarioService);
    campaignService = TestBed.inject(CampaignService);
    playerService = TestBed.inject(PlayerService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call Campaign query and add missing value', () => {
      const scenario: IScenario = { id: 456 };
      const campaign: ICampaign = { id: 25524 };
      scenario.campaign = campaign;

      const campaignCollection: ICampaign[] = [{ id: 97800 }];
      jest.spyOn(campaignService, 'query').mockReturnValue(of(new HttpResponse({ body: campaignCollection })));
      const additionalCampaigns = [campaign];
      const expectedCollection: ICampaign[] = [...additionalCampaigns, ...campaignCollection];
      jest.spyOn(campaignService, 'addCampaignToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ scenario });
      comp.ngOnInit();

      expect(campaignService.query).toHaveBeenCalled();
      expect(campaignService.addCampaignToCollectionIfMissing).toHaveBeenCalledWith(campaignCollection, ...additionalCampaigns);
      expect(comp.campaignsSharedCollection).toEqual(expectedCollection);
    });

    it('Should call Player query and add missing value', () => {
      const scenario: IScenario = { id: 456 };
      const players: IPlayer[] = [{ id: 62090 }];
      scenario.players = players;

      const playerCollection: IPlayer[] = [{ id: 17971 }];
      jest.spyOn(playerService, 'query').mockReturnValue(of(new HttpResponse({ body: playerCollection })));
      const additionalPlayers = [...players];
      const expectedCollection: IPlayer[] = [...additionalPlayers, ...playerCollection];
      jest.spyOn(playerService, 'addPlayerToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ scenario });
      comp.ngOnInit();

      expect(playerService.query).toHaveBeenCalled();
      expect(playerService.addPlayerToCollectionIfMissing).toHaveBeenCalledWith(playerCollection, ...additionalPlayers);
      expect(comp.playersSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const scenario: IScenario = { id: 456 };
      const campaign: ICampaign = { id: 62982 };
      scenario.campaign = campaign;
      const players: IPlayer = { id: 80555 };
      scenario.players = [players];

      activatedRoute.data = of({ scenario });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(scenario));
      expect(comp.campaignsSharedCollection).toContain(campaign);
      expect(comp.playersSharedCollection).toContain(players);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Scenario>>();
      const scenario = { id: 123 };
      jest.spyOn(scenarioService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ scenario });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: scenario }));
      saveSubject.complete();

      // THEN
      expect(comp.previousState).toHaveBeenCalled();
      expect(scenarioService.update).toHaveBeenCalledWith(scenario);
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Scenario>>();
      const scenario = new Scenario();
      jest.spyOn(scenarioService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ scenario });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: scenario }));
      saveSubject.complete();

      // THEN
      expect(scenarioService.create).toHaveBeenCalledWith(scenario);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Scenario>>();
      const scenario = { id: 123 };
      jest.spyOn(scenarioService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ scenario });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(scenarioService.update).toHaveBeenCalledWith(scenario);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Tracking relationships identifiers', () => {
    describe('trackCampaignById', () => {
      it('Should return tracked Campaign primary key', () => {
        const entity = { id: 123 };
        const trackResult = comp.trackCampaignById(0, entity);
        expect(trackResult).toEqual(entity.id);
      });
    });

    describe('trackPlayerById', () => {
      it('Should return tracked Player primary key', () => {
        const entity = { id: 123 };
        const trackResult = comp.trackPlayerById(0, entity);
        expect(trackResult).toEqual(entity.id);
      });
    });
  });

  describe('Getting selected relationships', () => {
    describe('getSelectedPlayer', () => {
      it('Should return option if no Player is selected', () => {
        const option = { id: 123 };
        const result = comp.getSelectedPlayer(option);
        expect(result === option).toEqual(true);
      });

      it('Should return selected Player for according option', () => {
        const option = { id: 123 };
        const selected = { id: 123 };
        const selected2 = { id: 456 };
        const result = comp.getSelectedPlayer(option, [selected2, selected]);
        expect(result === selected).toEqual(true);
        expect(result === selected2).toEqual(false);
        expect(result === option).toEqual(false);
      });

      it('Should return option if this Player is not selected', () => {
        const option = { id: 123 };
        const selected = { id: 456 };
        const result = comp.getSelectedPlayer(option, [selected]);
        expect(result === option).toEqual(true);
        expect(result === selected).toEqual(false);
      });
    });
  });
});
