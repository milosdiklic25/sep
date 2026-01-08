export interface CardPaymentRequest {
    orderId: string;
}

export interface CardPaymentResponse {
    redirectUrl: string;
}