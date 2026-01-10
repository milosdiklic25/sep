import { Component } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators, FormControl } from '@angular/forms';
import { AuthService } from '../auth.service';
import { SigninRequest } from '../../model/signinin-request.model';

@Component({
  selector: 'app-signup',
  standalone: true,
  imports: [ReactiveFormsModule],
  templateUrl: './signup.component.html',
  styleUrl: './signup.component.css'
})
export class SignupComponent {
  signupForm! : FormGroup;

  constructor(private service: AuthService, private formBuilder: FormBuilder){}

  ngOnInit(): void {
      this.signupForm = this.formBuilder.group({
        username: [null, [Validators.required]],
        password: [null, Validators.required]
      });
  }

  async submit(): Promise<void> {    
    const signinRequest : SigninRequest = {
      username: this.signupForm.value.username || "",
      password: this.signupForm.value.password || ""
    };
    this.service.register(signinRequest);
  }
}
