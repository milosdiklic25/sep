import { Routes } from '@angular/router';
import { SigninComponent } from './auth/signin/signin.component';
import { SignupComponent } from './auth/signup/signup.component';
import { VehiclesAllComponent } from './vehicles/vehicles-all/vehicles-all.component';
import { VehicleDetailsComponent } from './vehicles/vehicle-details/vehicle-details.component';
import { SuccessComponent } from './auth/success/success.component';
import { ErrorComponent } from './auth/error/error.component';
import { FailComponent } from './auth/fail/fail.component';

export const routes: Routes = [
    { path: '', redirectTo: 'vehicles', pathMatch: 'full' },
    { path: 'signin', component: SigninComponent },
    { path: 'signup', component: SignupComponent },
    { path: 'vehicles', component: VehiclesAllComponent },
    { path: 'vehicle/:id', component: VehicleDetailsComponent },
    { path: 'success', component: SuccessComponent },
    { path: 'error', component: ErrorComponent },
    { path: 'fail', component: FailComponent },
    { path: '**', redirectTo: 'vehicles' },
];
