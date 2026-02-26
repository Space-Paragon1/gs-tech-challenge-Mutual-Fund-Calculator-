import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MutualFundService } from '../../services/mutual-fund.service';
import { MutualFund, InvestmentProjection } from '../../models/investment.model';
import { CurrencyFormatPipe } from '../../pipes/currency-format.pipe';
import { InvestmentChartComponent, YearlyBreakdown } from '../investment-chart/investment-chart.component';

@Component({
  selector: 'app-calculator',
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule, CurrencyFormatPipe, InvestmentChartComponent],
  templateUrl: './calculator.component.html',
  styleUrls: ['./calculator.component.css']
})
export class CalculatorComponent implements OnInit {
  mutualFunds: MutualFund[] = [];
  investmentForm!: FormGroup;
  projection: InvestmentProjection | null = null;
  loading = false;
  loadingFunds = false;
  error: string | null = null;
  yearlyBreakdown: YearlyBreakdown[] = [];

  constructor(
    private mutualFundService: MutualFundService,
    private formBuilder: FormBuilder
  ) {}

  ngOnInit(): void {
    this.initializeForm();
    this.loadMutualFunds();
  }

  private initializeForm(): void {
    this.investmentForm = this.formBuilder.group({
      ticker: ['', Validators.required],
      principal: ['', [Validators.required, Validators.min(1)]],
      years: ['', [Validators.required, Validators.min(1), Validators.max(50)]]
    });
  }

  private loadMutualFunds(): void {
    this.loadingFunds = true;
    this.error = null;
    this.mutualFundService.getMutualFunds().subscribe({
      next: (funds) => {
        this.mutualFunds = funds;
        if (funds.length > 0) {
          this.investmentForm.patchValue({ ticker: funds[0].ticker });
        }
        this.loadingFunds = false;
      },
      error: (err) => {
        this.error = 'Failed to load mutual funds. Please try again later.';
        this.loadingFunds = false;
        console.error('Error loading mutual funds:', err);
      }
    });
  }

  calculateProjection(): void {
    if (this.investmentForm.invalid) {
      this.error = 'Please fill in all required fields with valid values.';
      return;
    }

    const { ticker, principal, years } = this.investmentForm.value;

    this.loading = true;
    this.error = null;
    this.projection = null;
    this.yearlyBreakdown = [];

    this.mutualFundService.getInvestmentProjection(ticker, principal, years).subscribe({
      next: (result) => {
        this.projection = result;
        this.yearlyBreakdown = this.computeBreakdown(result);
        this.loading = false;
      },
      error: (err) => {
        this.error = 'Failed to calculate projection. Please try again.';
        this.loading = false;
        console.error('Error calculating projection:', err);
      }
    });
  }

  private computeBreakdown(p: InvestmentProjection): YearlyBreakdown[] {
    const data: YearlyBreakdown[] = [];
    let prev = p.principal;
    for (let y = 0; y <= p.years; y++) {
      const value = p.principal * Math.pow(1 + p.capmRate, y);
      const gain = value - prev;
      const returnPct = y === 0 ? 0 : (gain / prev) * 100;
      data.push({
        year: y,
        value: Math.round(value * 100) / 100,
        gain: Math.round(gain * 100) / 100,
        returnPct: Math.round(returnPct * 100) / 100
      });
      prev = value;
    }
    return data;
  }

  resetForm(): void {
    this.investmentForm.reset();
    if (this.mutualFunds.length > 0) {
      this.investmentForm.patchValue({ ticker: this.mutualFunds[0].ticker });
    }
    this.projection = null;
    this.yearlyBreakdown = [];
    this.error = null;
  }

  get selectedFundName(): string {
    const ticker = this.investmentForm.get('ticker')?.value;
    const fund = this.mutualFunds.find(f => f.ticker === ticker);
    return fund ? fund.name : '';
  }

  get principal(): number {
    return this.investmentForm.get('principal')?.value || 0;
  }

  get futureValue(): number {
    return this.projection?.futureValue || 0;
  }

  get totalGain(): number {
    return this.futureValue - this.principal;
  }

  get gainPercentage(): number {
    return this.principal > 0 ? (this.totalGain / this.principal) * 100 : 0;
  }

  get annualizedReturn(): number {
    return this.projection ? this.projection.capmRate * 100 : 0;
  }
}
