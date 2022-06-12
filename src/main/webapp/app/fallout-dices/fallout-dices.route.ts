import { Route } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { FalloutDicesComponent } from './fallout-dices.component';

export const FALLOUT_DICES_ROUTES: Route = {
  path: 'fallout-dices',
  component: FalloutDicesComponent,
  data: {
    authorities: [],
    pageTitle: 'fallout-dices.title',
  },
  canActivate: [UserRouteAccessService],
};
