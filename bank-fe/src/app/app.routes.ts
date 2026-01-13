import { Routes } from '@angular/router';
import { PayComponent } from './pay/pay.component';
import { PayQrComponent } from './pay-qr/pay-qr.component';

export const routes: Routes = [
  { path: 'pay/qr/:paymentId', component: PayQrComponent },
  { path: 'pay/:paymentId', component: PayComponent },
  { path: '', redirectTo: 'pay/00000000-0000-0000-0000-000000000000', pathMatch: 'full' }
];