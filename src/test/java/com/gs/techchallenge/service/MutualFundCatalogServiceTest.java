package com.gs.techchallenge.service;

import com.gs.techchallenge.model.MutualFund;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class MutualFundCatalogServiceTest {

    private MutualFundCatalogService mutualFundCatalogService = new MutualFundCatalogService();

    @Test
    void getFundsReturnsNonEmptyList() {
        List<MutualFund> funds = mutualFundCatalogService.getFunds();

        assertThat(funds)
                .isNotEmpty()
                .hasSizeGreaterThanOrEqualTo(5);
    }

    @Test
    void getFundsReturnsValidMutualFunds() {
        List<MutualFund> funds = mutualFundCatalogService.getFunds();

        assertThat(funds).allMatch(fund ->
                fund.ticker() != null && !fund.ticker().isBlank() &&
                fund.name() != null && !fund.name().isBlank()
        );
    }

    @Test
    void isSupportedReturnsTrueForValidTicker() {
        assertThat(mutualFundCatalogService.isSupported("VFIAX")).isTrue();
        assertThat(mutualFundCatalogService.isSupported("FXAIX")).isTrue();
    }

    @Test
    void isSupportedReturnsFalseForInvalidTicker() {
        assertThat(mutualFundCatalogService.isSupported("INVALID")).isFalse();
    }

    @Test
    void isSupportedHandlesCaseInsensitivity() {
        assertThat(mutualFundCatalogService.isSupported("vfiax")).isTrue();
        assertThat(mutualFundCatalogService.isSupported("VfIaX")).isTrue();
    }

    @Test
    void getFundsContainsExpectedFunds() {
        List<MutualFund> funds = mutualFundCatalogService.getFunds();

        assertThat(funds)
                .extracting(MutualFund::ticker)
                .contains("VFIAX", "FXAIX", "SWPPX", "VTSAX", "VIGAX");
    }
}
