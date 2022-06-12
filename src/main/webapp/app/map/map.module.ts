import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { RouterModule } from '@angular/router';

import { SharedModule } from 'app/shared/shared.module';

import { MAP_ROUTE } from './map.route';
import { MapComponent } from './map.component';
import { AddEventDialogComponent } from './add-event-dialog/add-event-dialog.component';

@NgModule({
  imports: [SharedModule, RouterModule.forRoot([MAP_ROUTE], { useHash: true })],
  declarations: [MapComponent, AddEventDialogComponent],
  entryComponents: [],
  providers: [],
  schemas: [CUSTOM_ELEMENTS_SCHEMA],
})
export class JDRhelperAppMapModule {}
