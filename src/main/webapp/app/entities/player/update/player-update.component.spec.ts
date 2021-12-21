jest.mock('@angular/router');

import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { of, Subject } from 'rxjs';

import { PlayerService } from '../service/player.service';
import { IPlayer, Player } from '../player.model';
import { ICampaign } from 'app/entities/campaign/campaign.model';
import { CampaignService } from 'app/entities/campaign/service/campaign.service';

import { PlayerUpdateComponent } from './player-update.component';

describe('Player Management Update Component', () => {
  let comp: PlayerUpdateComponent;
  let fixture: ComponentFixture<PlayerUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let playerService: PlayerService;
  let campaignService: CampaignService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      declarations: [PlayerUpdateComponent],
      providers: [FormBuilder, ActivatedRoute],
    })
      .overrideTemplate(PlayerUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(PlayerUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    playerService = TestBed.inject(PlayerService);
    campaignService = TestBed.inject(CampaignService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call Campaign query and add missing value', () => {
      const player: IPlayer = { id: 456 };
      const campaign: ICampaign = { id: 55183 };
      player.campaign = campaign;

      const campaignCollection: ICampaign[] = [{ id: 43817 }];
      jest.spyOn(campaignService, 'query').mockReturnValue(of(new HttpResponse({ body: campaignCollection })));
      const additionalCampaigns = [campaign];
      const expectedCollection: ICampaign[] = [...additionalCampaigns, ...campaignCollection];
      jest.spyOn(campaignService, 'addCampaignToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ player });
      comp.ngOnInit();

      expect(campaignService.query).toHaveBeenCalled();
      expect(campaignService.addCampaignToCollectionIfMissing).toHaveBeenCalledWith(campaignCollection, ...additionalCampaigns);
      expect(comp.campaignsSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const player: IPlayer = { id: 456 };
      const campaign: ICampaign = { id: 87163 };
      player.campaign = campaign;

      activatedRoute.data = of({ player });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(player));
      expect(comp.campaignsSharedCollection).toContain(campaign);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Player>>();
      const player = { id: 123 };
      jest.spyOn(playerService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ player });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: player }));
      saveSubject.complete();

      // THEN
      expect(comp.previousState).toHaveBeenCalled();
      expect(playerService.update).toHaveBeenCalledWith(player);
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Player>>();
      const player = new Player();
      jest.spyOn(playerService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ player });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: player }));
      saveSubject.complete();

      // THEN
      expect(playerService.create).toHaveBeenCalledWith(player);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Player>>();
      const player = { id: 123 };
      jest.spyOn(playerService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ player });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(playerService.update).toHaveBeenCalledWith(player);
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
  });
});
