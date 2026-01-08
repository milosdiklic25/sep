export interface PayRequest {
  cardholderName: string;
  cardNumber: string;
  expiryDate: string;
  cvv: string;
}

export interface PspUpdateStatusResponse {
  redirectUrl: string;
}