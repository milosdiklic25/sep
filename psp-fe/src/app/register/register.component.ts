import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import {
  FormBuilder,
  FormGroup,
  ReactiveFormsModule,
  Validators
} from '@angular/forms';
import { PaymentApiService } from '../api/psp-api.service';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './register.component.html'
})
export class RegisterComponent {
  loading = false;
  serverError: string | null = null;

  merchantId: string | null = null;

  form: FormGroup;

  constructor(
    private fb: FormBuilder,
    private api: PaymentApiService
  ) {
    this.form = this.fb.group({
      name: ['', [Validators.required, Validators.minLength(2), Validators.maxLength(100)]],
      password: ['', [Validators.required, Validators.minLength(6), Validators.maxLength(200)]],
      errorUrl: ['', [Validators.required]],
      successUrl: ['', [Validators.required]],
      failUrl: ['', [Validators.required]],
    });
  }

  get f() {
    return this.form.controls as any;
  }

  register() {
    this.serverError = null;
    this.merchantId = null;

    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    this.loading = true;

    const body = {
      name: this.f.name.value,
      password: this.f.password.value,
      errorUrl: this.f.errorUrl.value,
      successUrl: this.f.successUrl.value,
      failUrl: this.f.failUrl.value,
    };

    this.api.register(body).subscribe({
      next: (res) => {
        this.merchantId = res.merchantId;
        this.loading = false;
      },
      error: (err) => {
        console.error(err);
        this.serverError = 'Registration failed. Please try again.';
        this.loading = false;
      }
    });
  }
}