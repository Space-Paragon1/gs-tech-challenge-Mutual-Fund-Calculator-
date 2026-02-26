package com.gs.techchallenge.service;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RiskFreeRateServiceTest {

    private RiskFreeRateService riskFreeRateService = new RiskFreeRateService();

    @Test
    void getRiskFreeRateReturnsValidRate() {
        double rate = riskFreeRateService.getRiskFreeRate();

        assertThat(rate)
                .isGreaterThanOrEqualTo(0)
                .isLessThanOrEqualTo(1);
    }

    @Test
    void getRiskFreeRateReturnsConsistentValue() {
        double rate1 = riskFreeRateService.getRiskFreeRate();
        double rate2 = riskFreeRateService.getRiskFreeRate();

        assertThat(rate1).isEqualTo(rate2);
    }

    @Test
    void getRiskFreeRateReturnsExpectedValue() {
        double rate = riskFreeRateService.getRiskFreeRate();

        assertThat(rate).isCloseTo(0.0425, org.assertj.core.data.Offset.offset(0.0001));
    }
}
