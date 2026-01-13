import { Injectable } from "@angular/core";
import { environment } from "../../environments/environment";
import { HttpClient } from "@angular/common/http";
import { CardPaymentRequest, CardPaymentResponse, RegisterMerchantRequest, RegisterMerchantResponse } from "./dtos";
import { Observable } from "rxjs";

@Injectable({ providedIn: 'root' })
export class PaymentApiService {
  private readonly baseUrl = environment.apiUrl;

  constructor(private http: HttpClient) {}

  pay(body: CardPaymentRequest): Observable<CardPaymentResponse> {
    return this.http.post<CardPaymentResponse>(
      `${this.baseUrl}/payments/card`,
      body
    );
  }

  payQr(body: CardPaymentRequest): Observable<CardPaymentResponse> {
    return this.http.post<CardPaymentResponse>(
      `${this.baseUrl}/payments/qr`,
      body
    );
  }

  register(body: RegisterMerchantRequest): Observable<RegisterMerchantResponse> {
    return this.http.post<RegisterMerchantResponse>(`${this.baseUrl}/merchants/register`, body);
  }
}

export type { CardPaymentRequest, CardPaymentResponse };
