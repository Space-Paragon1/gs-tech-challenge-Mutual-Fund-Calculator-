export interface MutualFund {
  ticker: string;
  name: string;
}

export interface InvestmentProjection {
  ticker: string;
  principal: number;
  years: number;
  riskFreeRate: number;
  beta: number;            // beta used in the calculation
  providerBeta: number;    // beta returned by the data provider
  betaAdjusted: boolean;   // true when providerBeta was unrealistic and 1.0 was used
  expectedReturnRate: number;
  capmRate: number;
  futureValue: number;
}
