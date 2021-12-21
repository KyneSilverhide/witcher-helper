jest.mock('@angular/router');

import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { of, Subject } from 'rxjs';

import { CampaignService } from '../service/campaign.service';
import { ICampaign, Campaign } from '../campaign.model';

import { CampaignUpdateComponent } from './campaign-update.component';

describe('Campaign Management Update Component', () => {
  let comp: CampaignUpdateComponent;
  let fixture: ComponentFixture<CampaignUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let campaignService: CampaignService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      declarations: [CampaignUpdateComponent],
      providers: [FormBuilder, ActivatedRoute],
    })
      .overrideTemplate(CampaignUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(CampaignUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    campaignService = TestBed.inject(CampaignService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should update editForm', () => {
      const campaign: ICampaign = { id: 456 };

      activatedRoute.data = of({ campaign });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(campaign));
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Campaign>>();
      const campaign = { id: 123 };
      jest.spyOn(campaignService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ campaign });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: campaign }));
      saveSubject.complete();

      // THEN
      expect(comp.previousState).toHaveBeenCalled();
      expect(campaignService.update).toHaveBeenCalledWith(campaign);
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Campaign>>();
      const campaign = new Campaign();
      jest.spyOn(campaignService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ campaign });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: campaign }));
      saveSubject.complete();

      // THEN
      expect(campaignService.create).toHaveBeenCalledWith(campaign);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Campaign>>();
      const campaign = { id: 123 };
      jest.spyOn(campaignService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ campaign });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(campaignService.update).toHaveBeenCalledWith(campaign);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
