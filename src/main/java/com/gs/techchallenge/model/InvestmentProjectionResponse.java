package com.gs.techchallenge.model;

import java.math.BigDecimal;

/**
 * @param beta         beta used in the CAPM calculation
 * @param providerBeta beta returned by the data provider
 * @param betaAdjusted true when providerBeta was unrealistic and the market beta (1.0) was used instead
 */
public record InvestmentProjectionResponse(
        String ticker,
        BigDecimal principal,
        int years,
        double riskFreeRate,
        double beta,
        double providerBeta,
        boolean betaAdjusted,
        double expectedReturnRate,
        double capmRate,
        BigDecimal futureValue
) {
}
