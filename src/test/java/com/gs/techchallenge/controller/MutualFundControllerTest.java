package com.gs.techchallenge.controller;

import com.gs.techchallenge.model.InvestmentProjectionResponse;
import com.gs.techchallenge.model.MutualFund;
import com.gs.techchallenge.service.InvestmentProjectionService;
import com.gs.techchallenge.service.MutualFundCatalogService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MutualFundControllerTest {

    @Mock
    private MutualFundCatalogService mutualFundCatalogService;

    @Mock
    private InvestmentProjectionService investmentProjectionService;

    @InjectMocks
    private MutualFundController mutualFundController;

    @Test
    void getMutualFundsReturnsListOfFunds() {
        List<MutualFund> expectedFunds = List.of(
                new MutualFund("VFIAX", "Vanguard 500 Index Fund"),
                new MutualFund("FXAIX", "Fidelity 500 Index Fund")
        );
        when(mutualFundCatalogService.getFunds()).thenReturn(expectedFunds);

        List<MutualFund> result = mutualFundController.getMutualFunds();

        assertThat(result).isEqualTo(expectedFunds);
        assertThat(result).hasSize(2);
    }

    @Test
    void getFutureValueReturnsProjectionForValidInput() {
        InvestmentProjectionResponse expectedResponse = new InvestmentProjectionResponse(
                "VFIAX",
                BigDecimal.valueOf(10000),
                5,
                0.04,
                1.2,
                0.10,
                0.112,
                BigDecimal.valueOf(17002.94)
        );

        when(mutualFundCatalogService.isSupported("VFIAX")).thenReturn(true);
        when(investmentProjectionService.projectFutureValue("VFIAX", BigDecimal.valueOf(10000), 5))
                .thenReturn(expectedResponse);

        InvestmentProjectionResponse result = mutualFundController.getFutureValue(
                "vfiax",
                BigDecimal.valueOf(10000),
                5
        );

        assertThat(result).isEqualTo(expectedResponse);
        assertThat(result.ticker()).isEqualTo("VFIAX");
        assertThat(result.futureValue()).isEqualByComparingTo("17002.94");
    }

    @Test
    void getFutureValueThrowsExceptionForUnsupportedTicker() {
        when(mutualFundCatalogService.isSupported("INVALID")).thenReturn(false);

        assertThatThrownBy(() -> mutualFundController.getFutureValue(
                "INVALID",
                BigDecimal.valueOf(10000),
                5
        ))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Unsupported mutual fund ticker");
    }

    @Test
    void getFutureValueNormalizeTickerToUppercase() {
        InvestmentProjectionResponse expectedResponse = new InvestmentProjectionResponse(
                "VFIAX",
                BigDecimal.valueOf(5000),
                3,
                0.04,
                1.0,
                0.08,
                0.08,
                BigDecimal.valueOf(6298.56)
        );

        when(mutualFundCatalogService.isSupported("VFIAX")).thenReturn(true);
        when(investmentProjectionService.projectFutureValue("VFIAX", BigDecimal.valueOf(5000), 3))
                .thenReturn(expectedResponse);

        InvestmentProjectionResponse result = mutualFundController.getFutureValue(
                "vfiax",
                BigDecimal.valueOf(5000),
                3
        );

        assertThat(result.ticker()).isEqualTo("VFIAX");
    }
}
