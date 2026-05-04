import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-analysis-landing',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './analysis-landing.component.html',
  styleUrl: './analysis-landing.component.scss'
})
export class AnalysisLandingComponent {}
