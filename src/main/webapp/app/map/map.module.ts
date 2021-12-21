import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { RouterModule } from '@angular/router';

import { SharedModule } from 'app/shared/shared.module';

import { MAP_ROUTE } from './map.route';
import { MapComponent } from './map.component';

@NgModule({
  imports: [SharedModule, RouterModule.forRoot([MAP_ROUTE], { useHash: true })],
  declarations: [MapComponent],
  entryComponents: [],
  providers: [],
  schemas: [CUSTOM_ELEMENTS_SCHEMA],
})
export class WitcherhelperAppMapModule {}
