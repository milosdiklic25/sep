import { Routes } from '@angular/router';
import { PayComponent } from './pay/pay.component';

export const routes: Routes = [
  { path: 'pay/:paymentId', component: PayComponent },
  { path: '', redirectTo: 'pay/00000000-0000-0000-0000-000000000000', pathMatch: 'full' }
];