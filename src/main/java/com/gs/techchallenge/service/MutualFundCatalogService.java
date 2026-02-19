package com.gs.techchallenge.service;

import com.gs.techchallenge.model.MutualFund;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class MutualFundCatalogService {

    private static final List<MutualFund> FUNDS = List.of(
            new MutualFund("VFIAX", "Vanguard 500 Index Fund Admiral Shares"),
            new MutualFund("FXAIX", "Fidelity 500 Index Fund"),
            new MutualFund("SWPPX", "Schwab S&P 500 Index Fund"),
            new MutualFund("VTSAX", "Vanguard Total Stock Market Index Fund Admiral Shares"),
            new MutualFund("VIGAX", "Vanguard Growth Index Fund Admiral Shares")
    );

    private static final Set<String> SUPPORTED_TICKERS = FUNDS.stream()
            .map(MutualFund::ticker)
            .map(t -> t.toUpperCase(Locale.ROOT))
            .collect(Collectors.toSet());

    public List<MutualFund> getFunds() {
        return FUNDS;
    }

    public boolean isSupported(String ticker) {
        return SUPPORTED_TICKERS.contains(ticker.toUpperCase(Locale.ROOT));
    }
}