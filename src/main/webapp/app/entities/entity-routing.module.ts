import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

@NgModule({
  imports: [
    RouterModule.forChild([
      {
        path: 'player',
        data: { pageTitle: 'jdrhelperApp.player.home.title' },
        loadChildren: () => import('./player/player.module').then(m => m.PlayerModule),
      },
      {
        path: 'campaign',
        data: { pageTitle: 'jdrhelperApp.campaign.home.title' },
        loadChildren: () => import('./campaign/campaign.module').then(m => m.CampaignModule),
      },
      {
        path: 'scenario',
        data: { pageTitle: 'jdrhelperApp.scenario.home.title' },
        loadChildren: () => import('./scenario/scenario.module').then(m => m.ScenarioModule),
      },
      /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
    ]),
  ],
})
export class EntityRoutingModule {}
