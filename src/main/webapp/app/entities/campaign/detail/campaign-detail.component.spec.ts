import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { CampaignDetailComponent } from './campaign-detail.component';

describe('Campaign Management Detail Component', () => {
  let comp: CampaignDetailComponent;
  let fixture: ComponentFixture<CampaignDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [CampaignDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ campaign: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(CampaignDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(CampaignDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load campaign on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.campaign).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
