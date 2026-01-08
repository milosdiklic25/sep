import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { PaymentApiService } from '../api/psp-api.service';

@Component({
  selector: 'app-options',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './options.component.html'
})
export class OptionsComponent {
  loading = false;
  serverError: string | null = null;

  orderId!: string;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private api: PaymentApiService
  ) {
    const id = this.route.snapshot.paramMap.get('orderId');
    if (!id) {
      this.serverError = 'Missing order id in URL.';
      return;
    }
    this.orderId = id;
  }

  qr() {
  }

  card() {
    this.serverError = null;

    if (!this.orderId) {
      this.serverError = 'Missing order id in URL.';
      return;
    }

    this.loading = true;

    this.api.pay({ orderId: this.orderId }).subscribe({
      next: (res) => {
        window.location.href = res.redirectUrl;
      },
      error: (err) => {
        console.error(err);
        this.serverError = 'Could not start card payment. Please try again.';
        this.loading = false;
      }
    });
  }
}
