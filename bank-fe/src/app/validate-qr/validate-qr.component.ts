import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute } from '@angular/router';
import { HttpClient, HttpClientModule } from '@angular/common/http';
import { firstValueFrom } from 'rxjs';
import { BrowserQRCodeReader } from '@zxing/browser';

type QrValidateResponse = { valid: boolean; message: string };

@Component({
  selector: 'app-validate-qr',
  standalone: true,
  imports: [CommonModule, HttpClientModule],
  templateUrl: './validate-qr.component.html',
  styleUrl: './validate-qr.component.css',
})
export class ValidateQrComponent {
  paymentId = '';
  decodedPayload: string | null = null;

  loading = false;
  error: string | null = null;

  apiResponse: QrValidateResponse | null = null;

  private readonly apiBase = 'http://localhost:8082/api/payments';

  constructor(private route: ActivatedRoute, private http: HttpClient) {
    this.paymentId = this.route.snapshot.paramMap.get('paymentId') ?? '';
  }

  async onFileSelected(event: Event) {
    this.resetState();

    const input = event.target as HTMLInputElement;
    const file = input.files?.[0];
    if (!file) return;

    try {
      this.loading = true;

      const reader = new BrowserQRCodeReader();
      const url = URL.createObjectURL(file);

      try {
        const result = await reader.decodeFromImageUrl(url);
        this.decodedPayload = result.getText().trim();
      } finally {
        URL.revokeObjectURL(url);
      }

      const body = { payload: this.decodedPayload };
      const resp = await firstValueFrom(
        this.http.post<QrValidateResponse>(
          `${this.apiBase}/qr/validate/${this.paymentId}`,
          body
        )
      );

      this.apiResponse = resp;
    } catch (e: any) {
      this.error =
        e?.message ??
        'Failed to decode QR or call backend. Check console/network.';
      console.error(e);
    } finally {
      this.loading = false;
    }
  }

  private resetState() {
    this.decodedPayload = null;
    this.apiResponse = null;
    this.error = null;
    this.loading = false;
  }
}