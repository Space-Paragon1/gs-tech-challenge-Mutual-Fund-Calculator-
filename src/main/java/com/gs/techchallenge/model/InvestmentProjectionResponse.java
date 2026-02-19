package com.gs.techchallenge.model;

import java.math.BigDecimal;

public record InvestmentProjectionResponse(
        String ticker,
        BigDecimal principal,
        int years,
        double riskFreeRate,
        double beta,
        double expectedReturnRate,
        double capmRate,
        BigDecimal futureValue
) {
}