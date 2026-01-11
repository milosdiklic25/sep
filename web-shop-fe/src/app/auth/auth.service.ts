import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { tap } from 'rxjs';
import { Inject, Injectable, PLATFORM_ID } from '@angular/core';
import { isPlatformBrowser } from '@angular/common';
import { SigninRequest } from '../model/signinin-request.model';
import { SignupRequest } from '../model/signup-request.model';

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  private apiUrl = 'http://localhost:8080/api/auth/';

  constructor(private http: HttpClient, private router: Router, @Inject(PLATFORM_ID) private platformId: Object) { }

  private isBrowser(): boolean {
    return isPlatformBrowser(this.platformId);
  }

  signin(signinRequest: SigninRequest) {
    return this.http.post<any>(this.apiUrl + 'signin', signinRequest).pipe(
      tap(response => {
        if (this.isBrowser()) {console.log("usao u if is browser")
          localStorage.setItem('accessToken', response.accessToken);
        }

        this.router.navigate(['/vehicles']);
      })
    );
  }

  signout(): void {
    localStorage.removeItem('accessToken');
  }

  register(signupRequest: SignupRequest) {
    return this.http.post<any>(this.apiUrl + 'signup', signupRequest).subscribe({
      next: () => {
        this.router.navigate(['/']);
      },
      error: (err) => {
        console.error('Sign up failed:', err);
      }
    });
  }

  base64UrlDecode(input: string): string {
    // Convert base64url -> base64
    let base64 = input.replace(/-/g, "+").replace(/_/g, "/");

    // Pad with '='
    const pad = base64.length % 4;
    if (pad) base64 += "=".repeat(4 - pad);

    // Decode base64 -> bytes -> utf-8 string
    const binary = atob(base64);
    const bytes = Uint8Array.from(binary, (c) => c.charCodeAt(0));
    return new TextDecoder().decode(bytes);
  }

  getLoggedInUser(): string {
    const token = localStorage.getItem("accessToken");
    const parts = token!.split(".");
    if (parts.length !== 3) throw new Error("Invalid JWT format");

    const payloadJson = this.base64UrlDecode(parts[1]);
    const payload = JSON.parse(payloadJson);
    const username = payload.sub;
    return username;
  }
}
