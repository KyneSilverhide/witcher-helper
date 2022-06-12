import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { RouterModule } from '@angular/router';

import { SharedModule } from 'app/shared/shared.module';

import { FALLOUT_DICES_ROUTES } from './fallout-dices.route';
import { FalloutDicesComponent } from './fallout-dices.component';

@NgModule({
  imports: [SharedModule, RouterModule.forRoot([FALLOUT_DICES_ROUTES], { useHash: true })],
  declarations: [FalloutDicesComponent],
  entryComponents: [],
  providers: [],
  schemas: [CUSTOM_ELEMENTS_SCHEMA],
})
export class JDRhelperAppFalloutDicesModule {}
