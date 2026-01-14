import { Component, OnDestroy, OnInit, ViewChild, ElementRef, inject } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { CommonModule } from '@angular/common';
import jsQR from 'jsqr';
import {
  AbstractControl,
  FormBuilder,
  FormGroup,
  ReactiveFormsModule,
  ValidationErrors,
  Validators
} from '@angular/forms';

type ParsedQr = {
  paymentCode: string;      // SF
  paymentPurpose: string;   // S
  accountNumber: string;    // R
  amount: string;           // I (currency+amount string)
  acquirer: string;         // N
};

type PayRequest = {
  cardholderName: string;
  cardNumber: string;
  expiryDate: string;
  cvv: string;
};

type PayResponse = { redirectUrl: string };

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
    year += 2000;
  }

  const now = new Date();
  const endOfExpiryMonth = new Date(year, month, 0, 23, 59, 59, 999);

  if (endOfExpiryMonth < now) return { expiry: 'expired' };
  if (year > now.getFullYear() + 25) return { expiry: 'future' };

  return null;
}

@Component({
  selector: 'app-pay-qr',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './pay-qr.component.html',
  styleUrl: './pay-qr.component.css',
})
export class PayQrComponent implements OnInit, OnDestroy {
  private http = inject(HttpClient);
  private route = inject(ActivatedRoute);
  private fb = inject(FormBuilder);

  private apiBaseUrl = 'http://localhost:8082/api/payments';

  paymentId: string | null = null;

  // QR
  loadingQr = false;
  scanning = false;
  error: string | null = null;
  qrObjectUrl: string | null = null;
  decodedText: string | null = null;
  parsed: ParsedQr | null = null;
  qrImageLoaded = false;

  @ViewChild('qrImg', { static: false }) qrImg?: ElementRef<HTMLImageElement>;
  @ViewChild('qrCanvas', { static: false }) qrCanvas?: ElementRef<HTMLCanvasElement>;

  // Form / payment submit
  form: FormGroup;
  loading = false;
  serverError: string | null = null;

  constructor() {
    this.form = this.fb.group({
      cardholderName: ['', [Validators.required, Validators.minLength(2), Validators.maxLength(100)]],
      cardNumber: ['', [Validators.required, luhnValidator]],
      expiryDate: ['', [Validators.required, expiryValidator]],
      cvv: ['', [Validators.required, Validators.pattern(/^\d{3,4}$/)]]
    });
  }

  ngOnInit(): void {
    const paymentId = this.route.snapshot.paramMap.get('paymentId');
    if (!paymentId) {
      this.error = 'Missing paymentId in route.';
      return;
    }
    this.paymentId = paymentId;
    this.fetchQr(paymentId);
  }

  ngOnDestroy(): void {
    this.revokeQrUrl();
  }

  get f() {
    return this.form.controls as any;
  }

  fetchQr(paymentId: string): void {
    this.loadingQr = true;
    this.error = null;

    // reset scan state whenever QR changes
    this.qrImageLoaded = false;
    this.decodedText = null;
    this.parsed = null;

    this.http.get(`${this.apiBaseUrl}/qr/${paymentId}`, { responseType: 'blob' }).subscribe({
      next: (blob) => {
        this.revokeQrUrl();
        this.qrObjectUrl = URL.createObjectURL(blob);
        this.loadingQr = false;
      },
      error: (err) => {
        this.loadingQr = false;
        this.error = err?.status ? `Failed to load QR (HTTP ${err.status}).` : 'Failed to load QR.';
      },
    });
  }

  onQrImageLoad(): void {
    this.qrImageLoaded = true;
  }

  scan(): void {
    this.error = null;
    this.decodedText = null;
    this.parsed = null;

    if (!this.qrObjectUrl) {
      this.error = 'QR not loaded yet.';
      return;
    }
    if (!this.qrImageLoaded) {
      this.error = 'QR image is still loading. Try again in a moment.';
      return;
    }

    const img = this.qrImg?.nativeElement;
    const canvas = this.qrCanvas?.nativeElement;

    if (!img || !canvas) {
      this.error = 'Scan elements not ready.';
      return;
    }

    this.scanning = true;

    try {
      const w = img.naturalWidth || img.width;
      const h = img.naturalHeight || img.height;

      canvas.width = w;
      canvas.height = h;

      const ctx = canvas.getContext('2d', { willReadFrequently: true });
      if (!ctx) throw new Error('Canvas context not available.');

      ctx.drawImage(img, 0, 0, w, h);

      const imageData = ctx.getImageData(0, 0, w, h);
      const result = jsQR(imageData.data, imageData.width, imageData.height);

      if (!result?.data) {
        this.error = 'Could not decode QR from the image.';
        return;
      }

      this.decodedText = result.data;
      this.parsed = parsePaymentQrPayload(result.data);
    } catch (e: any) {
      this.error = e?.message ?? 'Failed to decode QR.';
    } finally {
      this.scanning = false;
    }
  }

  submit(): void {
    this.serverError = null;

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

    const payUrl = `${this.apiBaseUrl}/pay/${this.paymentId}`;

    this.http.post<PayResponse>(payUrl, body).subscribe({
      next: (res) => {
        window.location.href = res.redirectUrl;
      },
      error: (err) => {
        console.error(err);
        this.serverError = 'Payment failed. Please try again.';
        this.loading = false;
      }
    });
  }

  private revokeQrUrl(): void {
    if (this.qrObjectUrl) {
      URL.revokeObjectURL(this.qrObjectUrl);
      this.qrObjectUrl = null;
    }
  }
}

function parsePaymentQrPayload(payload: string): ParsedQr {
  const parts = payload.split('|').map(p => p.trim()).filter(Boolean);

  const map: Record<string, string> = {};
  for (const part of parts) {
    const idx = part.indexOf(':');
    if (idx <= 0) continue;
    const key = part.slice(0, idx).trim();
    const value = part.slice(idx + 1).trim();
    map[key] = value;
  }

  return {
    paymentCode: map['SF'] ?? '',
    paymentPurpose: map['S'] ?? '',
    accountNumber: map['R'] ?? '',
    amount: map['I'] ?? '',
    acquirer: map['N'] ?? '',
  };
}