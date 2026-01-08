import { Routes } from '@angular/router';
import { OptionsComponent } from './options/options.component';

export const routes: Routes = [
  { path: 'options/:orderId', component: OptionsComponent },
  { path: '', redirectTo: 'options/00000000-0000-0000-0000-000000000000', pathMatch: 'full' }
];