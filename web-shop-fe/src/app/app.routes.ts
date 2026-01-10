import { Routes } from '@angular/router';
import { SigninComponent } from './auth/signin/signin.component';
import { SignupComponent } from './auth/signup/signup.component';
import { VehiclesAllComponent } from './vehicles/vehicles-all/vehicles-all.component';
import { VehicleDetailsComponent } from './vehicles/vehicle-details/vehicle-details.component';

export const routes: Routes = [
    { path: '', redirectTo: 'vehicles', pathMatch: 'full' },
    { path: 'signin', component: SigninComponent },
    { path: 'signup', component: SignupComponent },
    { path: 'vehicles', component: VehiclesAllComponent },
    { path: 'vehicle/:id', component: VehicleDetailsComponent },
    { path: '**', redirectTo: 'vehicles' },
];
