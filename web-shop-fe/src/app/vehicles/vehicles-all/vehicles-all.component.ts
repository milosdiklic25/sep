import { Component } from '@angular/core';
import { VehicleService } from '../vehicle.service';
import { AuthService } from '../../auth/auth.service';

@Component({
  selector: 'app-vehicles-all',
  standalone: true,
  imports: [],
  templateUrl: './vehicles-all.component.html',
  styleUrl: './vehicles-all.component.css'
})
export class VehiclesAllComponent {

  constructor(private service: VehicleService, private authService: AuthService) { }

  signout(): void {
    this.authService.signout();
  }
}
