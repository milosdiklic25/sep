import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { PayRequest, PspUpdateStatusResponse } from './dtos';
import { environment } from '../../environments/environment';

@Injectable({ providedIn: 'root' })
export class PaymentApiService {
  private readonly baseUrl = environment.apiUrl;

  constructor(private http: HttpClient) {}

  pay(paymentId: string, body: PayRequest): Observable<PspUpdateStatusResponse> {
    return this.http.post<PspUpdateStatusResponse>(
      `${this.baseUrl}/payments/pay/${paymentId}`,
      body
    );
  }
}

export type { PayRequest, PspUpdateStatusResponse };
