import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import {
  AbstractControl,
  FormBuilder,
  FormGroup,
  ReactiveFormsModule,
  ValidationErrors,
  Validators
} from '@angular/forms';
import { PaymentApiService, PayRequest } from '../api/payment-api.service';
import { ActivatedRoute, Router } from '@angular/router';

function luhnValidator(control: AbstractControl): ValidationErrors | null {
  const raw = (control.value ?? '').toString();
  const digits = raw.replace(/\s|-/g, '');

  if (!digits) return null; // let "required" handle empty
  if (!/^\d+$/.test(digits)) return { luhn: true };
  if (digits.length < 12 || digits.length > 19) return { luhn: true };

  let sum = 0;
  let shouldDouble = false;

  for (let i = digits.length - 1; i >= 0; i--) {
    let d = digits.charCodeAt(i) - 48;
    if (shouldDouble) {
      d *= 2;
      if (d > 9) d -= 9;
    }
    sum += d;
    shouldDouble = !shouldDouble;
  }

  return sum % 10 === 0 ? null : { luhn: true };
}

function expiryValidator(control: AbstractControl): ValidationErrors | null {
  const value = (control.value ?? '').toString().trim();
  if (!value) return null; // required handles empty

  // Accept: MM/YY or MM/YYYY
  const m = value.match(/^(\d{2})\s*\/\s*(\d{2}|\d{4})$/);
  if (!m) return { expiry: 'format' };

  const month = Number(m[1]);
  let year = Number(m[2]);

  if (month < 1 || month > 12) return { expiry: 'month' };

  if (m[2].length === 2) {
    // interpret 2-digit year as 2000-2099
    year += 2000;
  }

  // Expiry is usually end of the month
  const now = new Date();
  const endOfExpiryMonth = new Date(year, month, 0, 23, 59, 59, 999); // day 0 => last day of previous month; month is 1-based here
  // Explanation: new Date(year, month, 0) gives last day of month-1, so month=month gives correct last day for 1-based input

  if (endOfExpiryMonth < now) return { expiry: 'expired' };

  // Optional: block too-far-in-future years (PSPs often allow up to 20 years)
  if (year > now.getFullYear() + 25) return { expiry: 'future' };

  return null;
}

@Component({
  selector: 'app-pay',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './pay.component.html'
})
export class PayComponent {
  loading = false;
  serverError: string | null = null;

  form!: FormGroup;

  paymentId!: string;

  constructor(
    private fb: FormBuilder,
    private api: PaymentApiService,
    private route: ActivatedRoute,
    private router: Router
  ) {
    this.form = this.fb.group({
      cardholderName: ['', [Validators.required, Validators.minLength(2), Validators.maxLength(100)]],
      cardNumber: ['', [Validators.required, luhnValidator]],
      expiryDate: ['', [Validators.required, expiryValidator]],
      cvv: ['', [Validators.required, Validators.pattern(/^\d{3,4}$/)]]
    });

    const id = this.route.snapshot.paramMap.get('paymentId');
    if (!id) {
      this.serverError = 'Missing payment id in URL.';
      return;
    }
    this.paymentId = id;
  }


  get f() {
    // typed access is optional; this is fine for now
    return this.form.controls as any;
  }

  submit() {
    this.serverError = null;this.serverError = null;

    if (!this.paymentId) {
      this.serverError = 'Missing payment id in URL.';
      return;
    }

    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    const rawNumber = (this.f.cardNumber.value ?? '').replace(/\s|-/g, '');

    const body: PayRequest = {
      cardholderName: this.f.cardholderName.value,
      cardNumber: rawNumber,
      expiryDate: this.f.expiryDate.value,
      cvv: this.f.cvv.value
    };

    this.loading = true;
    this.api.pay(this.paymentId, body).subscribe({
      next: (res) => (window.location.href = res.redirectUrl),
      error: (err) => {
        console.error(err);
        this.serverError = 'Payment failed. Please try again.';
        this.loading = false;
      }
    });
  }
}