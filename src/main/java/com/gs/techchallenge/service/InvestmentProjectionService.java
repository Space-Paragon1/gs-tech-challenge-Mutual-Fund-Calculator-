package com.gs.techchallenge.service;

import com.gs.techchallenge.client.HistoricalReturnsClient;
import com.gs.techchallenge.client.NewtonAnalyticsClient;
import com.gs.techchallenge.model.InvestmentProjectionResponse;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class InvestmentProjectionService {

    private final NewtonAnalyticsClient newtonAnalyticsClient;
    private final HistoricalReturnsClient historicalReturnsClient;
    private final RiskFreeRateService riskFreeRateService;

    public InvestmentProjectionService(
            NewtonAnalyticsClient newtonAnalyticsClient,
            HistoricalReturnsClient historicalReturnsClient,
            RiskFreeRateService riskFreeRateService
    ) {
        this.newtonAnalyticsClient = newtonAnalyticsClient;
        this.historicalReturnsClient = historicalReturnsClient;
        this.riskFreeRateService = riskFreeRateService;
    }

    public InvestmentProjectionResponse projectFutureValue(String ticker, BigDecimal principal, int years) {
        double riskFreeRate = riskFreeRateService.getRiskFreeRate();
        double beta = newtonAnalyticsClient.getBeta(ticker);
        double expectedReturnRate = historicalReturnsClient.getExpectedAnnualReturnFromLastYear(ticker);

        double capmRate = riskFreeRate + beta * (expectedReturnRate - riskFreeRate);

        BigDecimal growthFactor = BigDecimal.valueOf(Math.pow(1 + capmRate, years));
        BigDecimal futureValue = principal.multiply(growthFactor).setScale(2, RoundingMode.HALF_UP);

        return new InvestmentProjectionResponse(
                ticker,
                principal,
                years,
                riskFreeRate,
                beta,
                expectedReturnRate,
                capmRate,
                futureValue
        );
    }
}