import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../services/auth.service';

@Component({
  selector: 'app-login',
  imports: [FormsModule],
  templateUrl: './login.component.html',
  styleUrl: './login.component.css'
})
export class LoginComponent {

  username = '';
  password = '';
  error = '';

  constructor(
    private auth: AuthService,
    private router: Router
  ) { }

  login() {
    this.auth.login(this.username, this.password).subscribe({
      next: () => this.router.navigate(['/projects']),
      error: () => this.error = 'Invalid credentials'
    });
  }
}