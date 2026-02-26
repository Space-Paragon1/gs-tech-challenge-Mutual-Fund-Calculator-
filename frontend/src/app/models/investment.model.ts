export interface MutualFund {
  ticker: string;
  name: string;
}

export interface InvestmentProjection {
  ticker: string;
  principal: number;
  years: number;
  riskFreeRate: number;
  beta: number;
  expectedReturnRate: number;
  capmRate: number;
  futureValue: number;
}
