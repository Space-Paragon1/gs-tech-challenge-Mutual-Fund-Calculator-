package com.gs.techchallenge.service;

import com.gs.techchallenge.client.HistoricalReturnsClient;
import com.gs.techchallenge.client.NewtonAnalyticsClient;
import com.gs.techchallenge.model.InvestmentProjectionResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InvestmentProjectionServiceTest {

    @Mock
    private NewtonAnalyticsClient newtonAnalyticsClient;

    @Mock
    private HistoricalReturnsClient historicalReturnsClient;

    @Mock
    private RiskFreeRateService riskFreeRateService;

    @InjectMocks
    private InvestmentProjectionService investmentProjectionService;

    @Test
    void projectFutureValueCalculatesCapmAndFutureValue() {
        when(riskFreeRateService.getRiskFreeRate()).thenReturn(0.04);
        when(newtonAnalyticsClient.getBeta("VFIAX")).thenReturn(1.2);
        when(historicalReturnsClient.getExpectedAnnualSp500ReturnFromFiveYears("VFIAX")).thenReturn(0.10);

        InvestmentProjectionResponse response =
                investmentProjectionService.projectFutureValue("VFIAX", BigDecimal.valueOf(10000), 5);

        assertThat(response.ticker()).isEqualTo("VFIAX");
        assertThat(response.principal()).isEqualByComparingTo("10000");
        assertThat(response.years()).isEqualTo(5);
        assertThat(response.riskFreeRate()).isEqualTo(0.04);
        assertThat(response.beta()).isEqualTo(1.2);
        assertThat(response.providerBeta()).isEqualTo(1.2);
        assertThat(response.betaAdjusted()).isFalse();
        assertThat(response.expectedReturnRate()).isEqualTo(0.10);
        assertThat(response.capmRate()).isCloseTo(0.112, org.assertj.core.data.Offset.offset(1e-12));
        assertThat(response.futureValue()).isEqualByComparingTo("17002.94");
    }

    @Test
    void unrealisticNegativeBetaFallsBackToMarketBeta() {
        // Actual value Newton Analytics returned for VFIAX (an S&P 500 index fund)
        when(riskFreeRateService.getRiskFreeRate()).thenReturn(0.04);
        when(newtonAnalyticsClient.getBeta("VFIAX")).thenReturn(-0.0875);
        when(historicalReturnsClient.getExpectedAnnualSp500ReturnFromFiveYears("VFIAX")).thenReturn(0.10);

        InvestmentProjectionResponse response =
                investmentProjectionService.projectFutureValue("VFIAX", BigDecimal.valueOf(10000), 5);

        assertThat(response.beta()).isEqualTo(1.0);
        assertThat(response.providerBeta()).isEqualTo(-0.0875);
        assertThat(response.betaAdjusted()).isTrue();
        // With beta 1.0, CAPM gives the market return
        assertThat(response.capmRate()).isCloseTo(0.10, org.assertj.core.data.Offset.offset(1e-12));
        assertThat(response.futureValue()).isEqualByComparingTo("16105.10");
    }

    @ParameterizedTest
    @ValueSource(doubles = {0.1, 0.49, 2.01, 5.0, Double.NaN, Double.POSITIVE_INFINITY})
    void betaOutsidePlausibleRangeFallsBackToMarketBeta(double providerBeta) {
        when(riskFreeRateService.getRiskFreeRate()).thenReturn(0.04);
        when(newtonAnalyticsClient.getBeta("VFIAX")).thenReturn(providerBeta);
        when(historicalReturnsClient.getExpectedAnnualSp500ReturnFromFiveYears("VFIAX")).thenReturn(0.10);

        InvestmentProjectionResponse response =
                investmentProjectionService.projectFutureValue("VFIAX", BigDecimal.valueOf(10000), 5);

        assertThat(response.beta()).isEqualTo(1.0);
        assertThat(response.betaAdjusted()).isTrue();
    }

    @ParameterizedTest
    @ValueSource(doubles = {0.5, 0.95, 1.3, 2.0})
    void betaInsidePlausibleRangeIsUsedAsIs(double providerBeta) {
        when(riskFreeRateService.getRiskFreeRate()).thenReturn(0.04);
        when(newtonAnalyticsClient.getBeta("VFIAX")).thenReturn(providerBeta);
        when(historicalReturnsClient.getExpectedAnnualSp500ReturnFromFiveYears("VFIAX")).thenReturn(0.10);

        InvestmentProjectionResponse response =
                investmentProjectionService.projectFutureValue("VFIAX", BigDecimal.valueOf(10000), 5);

        assertThat(response.beta()).isEqualTo(providerBeta);
        assertThat(response.betaAdjusted()).isFalse();
    }
}
