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
        assertThat(response.expectedReturnRate()).isEqualTo(0.10);
        assertThat(response.capmRate()).isCloseTo(0.112, org.assertj.core.data.Offset.offset(1e-12));
        assertThat(response.futureValue()).isEqualByComparingTo("17005.84");
    }
}
