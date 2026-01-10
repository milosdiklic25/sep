import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { AuthService } from '../auth.service';
import { SigninRequest } from '../../model/signinin-request.model';
import { provideHttpClient } from '@angular/common/http';

@Component({
  selector: 'app-signin',
  standalone: true,
  imports: [ReactiveFormsModule],
  templateUrl: './signin.component.html',
  styleUrl: './signin.component.css'
})
export class SigninComponent implements OnInit{

  signinForm! : FormGroup;

  constructor(private service: AuthService, private formBuilder: FormBuilder) {}

  ngOnInit(): void {
      this.signinForm = this.formBuilder.group({
        username: [null, Validators.required],
        password: [null, Validators.required]
      })
  }

  async submit(): Promise<void> {
    const signinRequest : SigninRequest = {
      username: this.signinForm.value.username || "",
      password: this.signinForm.value.password || "",
    };
  
    this.service.signin(signinRequest).subscribe({
      next: (response) => {
        //console.log('Sign in successful', response);
      },
      error: (err) => {
        //console.error('Sign in failed', err);
      }
    });
  }
}
