import { Route } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { MapComponent } from './map.component';

export const MAP_ROUTE: Route = {
  path: 'map',
  component: MapComponent,
  data: {
    authorities: [],
    pageTitle: 'map.title',
  },
  canActivate: [UserRouteAccessService],
};
