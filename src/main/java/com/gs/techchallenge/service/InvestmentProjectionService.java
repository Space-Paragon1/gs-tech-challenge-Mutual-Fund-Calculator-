package com.gs.techchallenge.service;

import com.gs.techchallenge.client.HistoricalReturnsClient;
import com.gs.techchallenge.client.NewtonAnalyticsClient;
import com.gs.techchallenge.model.InvestmentProjectionResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class InvestmentProjectionService {

    private static final Logger log = LoggerFactory.getLogger(InvestmentProjectionService.class);

    // Every fund in the catalog is a diversified US stock fund, so its beta
    // against the S&P 500 should be close to 1. A provider value outside this
    // (deliberately generous) range is treated as bad data.
    static final double MIN_PLAUSIBLE_BETA = 0.5;
    static final double MAX_PLAUSIBLE_BETA = 2.0;
    static final double MARKET_BETA = 1.0;

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
        double providerBeta = newtonAnalyticsClient.getBeta(ticker);
        double expectedReturnRate = historicalReturnsClient.getExpectedAnnualSp500ReturnFromFiveYears(ticker);

        boolean betaAdjusted = !isPlausibleBeta(providerBeta);
        double beta = betaAdjusted ? MARKET_BETA : providerBeta;
        if (betaAdjusted) {
            log.warn("Unrealistic beta {} from provider for {}; using market beta {}", providerBeta, ticker, MARKET_BETA);
        }

        double capmRate = riskFreeRate + beta * (expectedReturnRate - riskFreeRate);

        BigDecimal growthFactor = BigDecimal.valueOf(Math.pow(1 + capmRate, years));
        BigDecimal futureValue = principal.multiply(growthFactor).setScale(2, RoundingMode.HALF_UP);

        return new InvestmentProjectionResponse(
                ticker,
                principal,
                years,
                riskFreeRate,
                beta,
                providerBeta,
                betaAdjusted,
                expectedReturnRate,
                capmRate,
                futureValue
        );
    }

    private static boolean isPlausibleBeta(double beta) {
        return Double.isFinite(beta) && beta >= MIN_PLAUSIBLE_BETA && beta <= MAX_PLAUSIBLE_BETA;
    }
}
