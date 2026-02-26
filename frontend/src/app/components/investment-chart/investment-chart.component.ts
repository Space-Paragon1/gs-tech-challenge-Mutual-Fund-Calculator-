import {
  Component, Input, OnChanges, SimpleChanges,
  ViewChild, ElementRef, AfterViewInit, OnDestroy
} from '@angular/core';
import { CommonModule } from '@angular/common';
import { Chart, registerables } from 'chart.js';
import { InvestmentProjection } from '../../models/investment.model';

Chart.register(...registerables);

export interface YearlyBreakdown {
  year: number;
  value: number;
  gain: number;
  returnPct: number;
}

@Component({
  selector: 'app-investment-chart',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="chart-container">
      <div class="chart-header">
        <div>
          <h3 class="chart-title">{{ title }}</h3>
          <p class="chart-subtitle">{{ subtitle }}</p>
        </div>
        <div class="period-toggle" *ngIf="showPeriodToggle">
          <button *ngFor="let p of periods"
            class="period-btn"
            [class.active]="activePeriod === p"
            (click)="setPeriod(p)">
            {{ p }}Y
          </button>
        </div>
      </div>
      <div class="chart-body">
        <canvas #chartCanvas></canvas>
      </div>
    </div>
  `,
  styles: [`
    .chart-container {
      background: var(--bg-card);
      border: 1px solid var(--border-color);
      border-radius: var(--radius-lg);
      padding: 24px;
      height: 100%;
    }
    .chart-header {
      display: flex;
      justify-content: space-between;
      align-items: flex-start;
      margin-bottom: 20px;
    }
    .chart-title {
      font-size: 0.95rem;
      font-weight: 600;
      color: var(--text-primary);
      margin-bottom: 4px;
    }
    .chart-subtitle {
      font-size: 0.78rem;
      color: var(--text-muted);
    }
    .period-toggle {
      display: flex;
      gap: 4px;
      background: var(--bg-input);
      border-radius: var(--radius-sm);
      padding: 3px;
    }
    .period-btn {
      font-family: 'Inter', sans-serif;
      font-size: 0.72rem;
      font-weight: 600;
      color: var(--text-muted);
      background: transparent;
      border: none;
      padding: 5px 12px;
      border-radius: 6px;
      cursor: pointer;
      transition: all 0.2s;
    }
    .period-btn:hover { color: var(--text-secondary); }
    .period-btn.active {
      color: var(--text-primary);
      background: var(--accent-primary);
    }
    .chart-body {
      position: relative;
      width: 100%;
      height: 260px;
    }
    .chart-body canvas {
      width: 100% !important;
      height: 100% !important;
    }
  `]
})
export class InvestmentChartComponent implements AfterViewInit, OnChanges, OnDestroy {
  @ViewChild('chartCanvas') chartCanvas!: ElementRef<HTMLCanvasElement>;

  @Input() projection!: InvestmentProjection;
  @Input() chartType: 'growth' | 'returns' = 'growth';
  @Input() title = '';
  @Input() subtitle = '';
  @Input() showPeriodToggle = false;

  periods = [1, 5, 10];
  activePeriod = 5;
  private chart: Chart | null = null;
  private initialized = false;

  ngAfterViewInit(): void {
    this.initialized = true;
    if (this.projection) {
      this.renderChart();
    }
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (this.initialized && changes['projection']) {
      this.renderChart();
    }
  }

  ngOnDestroy(): void {
    this.chart?.destroy();
  }

  setPeriod(p: number): void {
    this.activePeriod = p;
    this.renderChart();
  }

  private getYearlyBreakdown(years: number): YearlyBreakdown[] {
    const data: YearlyBreakdown[] = [];
    const rate = this.projection.capmRate;
    const principal = this.projection.principal;
    let prev = principal;

    for (let y = 0; y <= years; y++) {
      const value = principal * Math.pow(1 + rate, y);
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

  private renderChart(): void {
    if (!this.chartCanvas || !this.projection) return;
    this.chart?.destroy();

    const years = this.showPeriodToggle ? this.activePeriod : this.projection.years;
    const breakdown = this.getYearlyBreakdown(years);
    const labels = breakdown.map(b => b.year === 0 ? 'Now' : `Yr ${b.year}`);

    if (this.chartType === 'growth') {
      this.renderGrowthChart(labels, breakdown);
    } else {
      this.renderReturnsChart(labels, breakdown);
    }
  }

  private renderGrowthChart(labels: string[], data: YearlyBreakdown[]): void {
    const ctx = this.chartCanvas.nativeElement.getContext('2d')!;

    const gradient = ctx.createLinearGradient(0, 0, 0, 260);
    gradient.addColorStop(0, 'rgba(99, 102, 241, 0.25)');
    gradient.addColorStop(1, 'rgba(99, 102, 241, 0.0)');

    const principalLine = data.map(() => this.projection.principal);

    this.chart = new Chart(ctx, {
      type: 'line',
      data: {
        labels,
        datasets: [
          {
            label: 'Portfolio Value',
            data: data.map(d => d.value),
            borderColor: '#6366f1',
            backgroundColor: gradient,
            fill: true,
            tension: 0.35,
            borderWidth: 2.5,
            pointRadius: 0,
            pointHoverRadius: 6,
            pointHoverBackgroundColor: '#6366f1',
            pointHoverBorderColor: '#fff',
            pointHoverBorderWidth: 2
          },
          {
            label: 'Initial Investment',
            data: principalLine,
            borderColor: 'rgba(148, 163, 184, 0.3)',
            borderDash: [6, 4],
            borderWidth: 1.5,
            pointRadius: 0,
            fill: false
          }
        ]
      },
      options: this.getChartOptions('$')
    });
  }

  private renderReturnsChart(labels: string[], data: YearlyBreakdown[]): void {
    const ctx = this.chartCanvas.nativeElement.getContext('2d')!;

    // Annual dollar gains grow each year due to compounding — much more informative
    const annualGains = data.slice(1).map(d => d.gain);
    // Cumulative gain from start
    const cumulativeGains = data.slice(1).map(d => d.value - this.projection.principal);

    const gradient = ctx.createLinearGradient(0, 0, 0, 260);
    gradient.addColorStop(0, 'rgba(16, 185, 129, 0.65)');
    gradient.addColorStop(1, 'rgba(16, 185, 129, 0.15)');

    this.chart = new Chart(ctx, {
      type: 'bar',
      data: {
        labels: labels.slice(1),
        datasets: [
          {
            type: 'bar',
            label: 'Annual Gain',
            data: annualGains,
            backgroundColor: gradient,
            borderColor: '#10b981',
            borderWidth: 1,
            borderRadius: 6,
            borderSkipped: false,
            yAxisID: 'y'
          },
          {
            type: 'line',
            label: 'Cumulative Gain',
            data: cumulativeGains,
            borderColor: '#f59e0b',
            backgroundColor: 'rgba(245, 158, 11, 0.08)',
            fill: false,
            tension: 0.35,
            borderWidth: 2,
            pointRadius: 3,
            pointBackgroundColor: '#f59e0b',
            pointBorderColor: '#1a2035',
            pointBorderWidth: 1.5,
            yAxisID: 'y1'
          }
        ]
      },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        interaction: { intersect: false, mode: 'index' },
        plugins: {
          legend: {
            display: true,
            position: 'bottom',
            labels: {
              color: '#64748b',
              font: { family: 'Inter', size: 11 },
              boxWidth: 12,
              padding: 16
            }
          },
          tooltip: {
            backgroundColor: '#1a2035',
            titleColor: '#f1f5f9',
            bodyColor: '#94a3b8',
            borderColor: '#1e293b',
            borderWidth: 1,
            padding: 12,
            cornerRadius: 8,
            titleFont: { family: 'Inter', weight: 'bold', size: 13 },
            bodyFont: { family: 'Inter', size: 12 },
            callbacks: {
              label: (c: any) => {
                const val = c.parsed.y;
                return ` ${c.dataset.label}: $${val.toLocaleString('en-US', { minimumFractionDigits: 2 })}`;
              }
            }
          }
        },
        scales: {
          x: {
            grid: { color: 'rgba(30, 41, 59, 0.5)', display: true },
            ticks: { color: '#64748b', font: { family: 'Inter', size: 11 } }
          },
          y: {
            position: 'left',
            title: { display: true, text: 'Annual Gain', color: '#10b981', font: { family: 'Inter', size: 11 } },
            grid: { color: 'rgba(30, 41, 59, 0.5)', display: true },
            ticks: {
              color: '#64748b',
              font: { family: 'Inter', size: 11 },
              callback: (value: string | number) => `$${this.formatCompact(+value)}`
            }
          },
          y1: {
            position: 'right',
            title: { display: true, text: 'Cumulative', color: '#f59e0b', font: { family: 'Inter', size: 11 } },
            grid: { drawOnChartArea: false },
            ticks: {
              color: '#64748b',
              font: { family: 'Inter', size: 11 },
              callback: (value: string | number) => `$${this.formatCompact(+value)}`
            }
          }
        }
      }
    });
  }

  private formatCompact(value: number): string {
    if (Math.abs(value) >= 1_000_000) return `${(value / 1_000_000).toFixed(1)}M`;
    if (Math.abs(value) >= 1_000) return `${(value / 1_000).toFixed(1)}k`;
    return value.toFixed(0);
  }

  private getChartOptions(unit: string): any {
    return {
      responsive: true,
      maintainAspectRatio: false,
      interaction: {
        intersect: false,
        mode: 'index'
      },
      plugins: {
        legend: { display: false },
        tooltip: {
          backgroundColor: '#1a2035',
          titleColor: '#f1f5f9',
          bodyColor: '#94a3b8',
          borderColor: '#1e293b',
          borderWidth: 1,
          padding: 12,
          cornerRadius: 8,
          titleFont: { family: 'Inter', weight: 'bold', size: 13 },
          bodyFont: { family: 'Inter', size: 12 },
          callbacks: {
            label: (ctx: any) => {
              const val = ctx.parsed.y;
              return ` ${ctx.dataset.label}: $${val.toLocaleString('en-US', { minimumFractionDigits: 2 })}`;
            }
          }
        }
      },
      scales: {
        x: {
          grid: { color: 'rgba(30, 41, 59, 0.5)', display: true },
          ticks: {
            color: '#64748b',
            font: { family: 'Inter', size: 11 }
          }
        },
        y: {
          grid: { color: 'rgba(30, 41, 59, 0.5)', display: true },
          ticks: {
            color: '#64748b',
            font: { family: 'Inter', size: 11 },
            callback: (value: string | number) => `$${this.formatCompact(+value)}`
          }
        }
      }
    };
  }
}
