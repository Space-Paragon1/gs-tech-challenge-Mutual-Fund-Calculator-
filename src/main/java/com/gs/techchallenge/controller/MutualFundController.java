package com.gs.techchallenge.controller;

import com.gs.techchallenge.model.InvestmentProjectionResponse;
import com.gs.techchallenge.model.MutualFund;
import com.gs.techchallenge.service.InvestmentProjectionService;
import com.gs.techchallenge.service.MutualFundCatalogService;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@Validated
@RestController
@RequestMapping("/api")
public class MutualFundController {

    private final MutualFundCatalogService mutualFundCatalogService;
    private final InvestmentProjectionService investmentProjectionService;

    public MutualFundController(
            MutualFundCatalogService mutualFundCatalogService,
            InvestmentProjectionService investmentProjectionService
    ) {
        this.mutualFundCatalogService = mutualFundCatalogService;
        this.investmentProjectionService = investmentProjectionService;
    }

    @GetMapping("/mutual-funds")
    public List<MutualFund> getMutualFunds() {
        return mutualFundCatalogService.getFunds();
    }

    @GetMapping("/investments/future-value")
    public InvestmentProjectionResponse getFutureValue(
            @RequestParam @NotBlank String ticker,
            @RequestParam @Positive BigDecimal principal,
            @RequestParam @Min(1) int years
    ) {
        String normalizedTicker = ticker.trim().toUpperCase(Locale.ROOT);
        if (!mutualFundCatalogService.isSupported(normalizedTicker)) {
            throw new ResponseStatusException(BAD_REQUEST, "Unsupported mutual fund ticker: " + normalizedTicker);
        }

        return investmentProjectionService.projectFutureValue(normalizedTicker, principal, years);
    }
}