export interface CardPaymentRequest {
    orderId: string;
}

export interface CardPaymentResponse {
    redirectUrl: string;
}

export interface RegisterMerchantRequest {
  name: string;
  password: string;
  errorUrl: string;
  successUrl: string;
  failUrl: string;
}

export interface RegisterMerchantResponse {
  merchantId: string;
}