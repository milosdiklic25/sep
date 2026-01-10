import { Component } from '@angular/core';
import { ActivatedRoute, RouterModule } from '@angular/router';
import { Observable, switchMap } from 'rxjs';
import { Vehicle } from '../../model/vehicle.model';
import { VehicleService } from '../vehicle.service';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-vehicle-details',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './vehicle-details.component.html',
  styleUrl: './vehicle-details.component.css'
})
export class VehicleDetailsComponent {
  vehicle$!: Observable<Vehicle>;

  constructor(private route: ActivatedRoute, private vehicleService: VehicleService) {
    this.vehicle$ = this.route.paramMap.pipe(
      switchMap(params => {
        const id = params.get('id') ?? '';
        return this.vehicleService.getById(id); // id is UUID string
      })
    );
  }
}
