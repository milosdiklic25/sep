import { Component } from '@angular/core';
import { VehicleService } from '../vehicle.service';
import { AuthService } from '../../auth/auth.service';
import { Observable } from 'rxjs';
import { Vehicle } from '../../model/vehicle.model';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';

@Component({
  selector: 'app-vehicles-all',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './vehicles-all.component.html',
  styleUrl: './vehicles-all.component.css'
})
export class VehiclesAllComponent {
  vehicles$: Observable<Vehicle[]>;

  constructor(private service: VehicleService, private authService: AuthService, private router: Router) {
    this.vehicles$ = this.service.getAll();
  }

  goToDetails(id: string) {console.log('clicked', id);
    this.router.navigate(['/vehicle', id]);
  }

  signout(): void {
    this.authService.signout();
  }
}
