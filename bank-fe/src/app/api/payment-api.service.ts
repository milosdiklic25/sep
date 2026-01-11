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

  getAmount(paymentId: string): Observable<number> {
    return this.http.get<number>(`${this.baseUrl}/payments/amount/${paymentId}`);
  }
}

export type { PayRequest, PspUpdateStatusResponse };
