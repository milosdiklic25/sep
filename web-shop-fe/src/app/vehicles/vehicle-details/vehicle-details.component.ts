import { Component } from '@angular/core';
import { ActivatedRoute, RouterModule } from '@angular/router';
import { Observable, switchMap } from 'rxjs';
import { Vehicle } from '../../model/vehicle.model';
import { VehicleService } from '../vehicle.service';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';

@Component({
  selector: 'app-vehicle-details',
  standalone: true,
  imports: [CommonModule, RouterModule, ReactiveFormsModule],
  templateUrl: './vehicle-details.component.html',
  styleUrl: './vehicle-details.component.css'
})
export class VehicleDetailsComponent {
  vehicle$!: Observable<Vehicle>;
  message = '';
  reserving = false;

  reserveForm = new FormGroup({
    startDate: new FormControl<Date>(new Date(), { nonNullable: true, validators: [Validators.required] }),
    endDate: new FormControl<Date>(new Date(), { nonNullable: true, validators: [Validators.required] }),
  });

  constructor(private route: ActivatedRoute, private vehicleService: VehicleService) {
    this.vehicle$ = this.route.paramMap.pipe(
      switchMap(params => {
        const id = params.get('id') ?? '';
        return this.vehicleService.getById(id); // id is UUID string
      })
    );
  }

  reserve(vehicleId: string) {

    if (this.reserveForm.invalid) {
      this.message = 'Please choose start and end date.';
      return;
    }

    const startDate = this.reserveForm.value.startDate!;
    const endDate = this.reserveForm.value.endDate!;

    this.reserving = true;
    this.vehicleService.reserve({
      vehicleId,
      userId: 'a7503118-1989-46e4-9632-ae44d78e7529', 
      startDate,
      endDate,
    }).subscribe({
      next: () => {
        this.message = 'Reserved!';
        this.reserving = false;
      },
      error: (err) => {
        this.message = `Reserve failed (${err.status})`;
        this.reserving = false;
      }
    });
  }
}
