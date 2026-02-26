import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { MutualFund, InvestmentProjection } from '../models/investment.model';

@Injectable({
  providedIn: 'root'
})
export class MutualFundService {
  private apiUrl = '/api';

  constructor(private http: HttpClient) {}

  getMutualFunds(): Observable<MutualFund[]> {
    return this.http.get<MutualFund[]>(`${this.apiUrl}/mutual-funds`);
  }

  getInvestmentProjection(
    ticker: string,
    principal: number,
    years: number
  ): Observable<InvestmentProjection> {
    return this.http.get<InvestmentProjection>(
      `${this.apiUrl}/investments/future-value`,
      {
        params: {
          ticker: ticker,
          principal: principal.toString(),
          years: years.toString()
        }
      }
    );
  }
}
