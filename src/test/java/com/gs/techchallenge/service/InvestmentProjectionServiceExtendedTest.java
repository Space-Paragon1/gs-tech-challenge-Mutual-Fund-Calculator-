package com.gs.techchallenge.service;

import com.gs.techchallenge.client.HistoricalReturnsClient;
import com.gs.techchallenge.client.NewtonAnalyticsClient;
import com.gs.techchallenge.model.InvestmentProjectionResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InvestmentProjectionServiceExtendedTest {

    @Mock
    private NewtonAnalyticsClient newtonAnalyticsClient;

    @Mock
    private HistoricalReturnsClient historicalReturnsClient;

    @Mock
    private RiskFreeRateService riskFreeRateService;

    @InjectMocks
    private InvestmentProjectionService investmentProjectionService;

    @Test
    void projectFutureValueWithDifferentTimeHorizons() {
        when(riskFreeRateService.getRiskFreeRate()).thenReturn(0.04);
        when(newtonAnalyticsClient.getBeta("VFIAX")).thenReturn(1.0);
        when(historicalReturnsClient.getExpectedAnnualSp500ReturnFromFiveYears("VFIAX")).thenReturn(0.08);

        InvestmentProjectionResponse response1 = investmentProjectionService.projectFutureValue("VFIAX", BigDecimal.valueOf(1000), 1);
        InvestmentProjectionResponse response10 = investmentProjectionService.projectFutureValue("VFIAX", BigDecimal.valueOf(1000), 10);

        assertThat(response1.futureValue()).isLessThan(response10.futureValue());
    }

    @Test
    void projectFutureValueWithHighBeta() {
        when(riskFreeRateService.getRiskFreeRate()).thenReturn(0.04);
        when(newtonAnalyticsClient.getBeta("VIGAX")).thenReturn(1.5); // High beta
        when(historicalReturnsClient.getExpectedAnnualSp500ReturnFromFiveYears("VIGAX")).thenReturn(0.10);

        InvestmentProjectionResponse response = investmentProjectionService.projectFutureValue("VIGAX", BigDecimal.valueOf(10000), 5);

        assertThat(response.beta()).isGreaterThan(1);
        assertThat(response.capmRate()).isGreaterThan(0.04);
    }

    @Test
    void projectFutureValueHandesLargeAmounts() {
        when(riskFreeRateService.getRiskFreeRate()).thenReturn(0.04);
        when(newtonAnalyticsClient.getBeta("VFIAX")).thenReturn(1.0);
        when(historicalReturnsClient.getExpectedAnnualSp500ReturnFromFiveYears("VFIAX")).thenReturn(0.08);

        InvestmentProjectionResponse response = investmentProjectionService.projectFutureValue("VFIAX", BigDecimal.valueOf(1000000), 10);

        assertThat(response.principal()).isEqualByComparingTo("1000000");
        assertThat(response.futureValue()).isGreaterThan(response.principal());
    }

    @Test
    void projectFutureValueMaintainsPrecision() {
        when(riskFreeRateService.getRiskFreeRate()).thenReturn(0.04);
        when(newtonAnalyticsClient.getBeta("VFIAX")).thenReturn(1.0);
        when(historicalReturnsClient.getExpectedAnnualSp500ReturnFromFiveYears("VFIAX")).thenReturn(0.08);

        InvestmentProjectionResponse response = investmentProjectionService.projectFutureValue("VFIAX", BigDecimal.valueOf(10000), 5);

        // Check that future value has exactly 2 decimal places (currency precision)
        assertThat(response.futureValue().scale()).isEqualTo(2);
    }
}
