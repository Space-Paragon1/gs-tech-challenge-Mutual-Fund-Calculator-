package com.gs.techchallenge.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gs.techchallenge.exception.ExternalApiException;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@Component
public class HistoricalReturnsClient {

    private static final String MODERN_PORTFOLIO_URL = "https://api.newtonanalytics.com/modern-portfolio/";

    private final RestClient restClient;
    private final ObjectMapper objectMapper;

    public HistoricalReturnsClient(ObjectMapper objectMapper) {
        this.restClient = RestClient.create();
        this.objectMapper = objectMapper;
    }

    public double getExpectedAnnualReturnFromLastYear(String ticker) {
        URI uri = UriComponentsBuilder.fromHttpUrl(MODERN_PORTFOLIO_URL)
                .queryParam("tickers", ticker + ",^GSPC")
                .queryParam("interval", "1mo")
                .queryParam("observations", "12")
                .encode()
                .build()
                .toUri();

        try {
            String responseBody = restClient.get()
                    .uri(uri)
                    .retrieve()
                    .body(String.class);
            double averageMonthlyReturn = parseAverageMonthlyReturn(responseBody);
            return Math.pow(1 + averageMonthlyReturn, 12) - 1;
        } catch (Exception ex) {
            throw new ExternalApiException("Unable to fetch historical returns for " + ticker, ex);
        }
    }

    private double parseAverageMonthlyReturn(String responseBody) {
        if (responseBody == null || responseBody.isBlank()) {
            throw new ExternalApiException("Newton Modern Portfolio API returned an empty response");
        }

        try {
            JsonNode root = objectMapper.readTree(responseBody);
            JsonNode statusNode = root.get("status");
            if (statusNode != null && !"200".equals(statusNode.asText())) {
                String errorMessage = root.path("data").asText("Newton Modern Portfolio API unsuccessful");
                throw new ExternalApiException(errorMessage);
            }

            JsonNode avgReturnsNode = root.get("averageReturns");
            if (avgReturnsNode == null || !avgReturnsNode.isArray() || avgReturnsNode.isEmpty()) {
                throw new ExternalApiException("Modern Portfolio response missing averageReturns");
            }

            JsonNode selectedFundReturn = avgReturnsNode.get(0);
            if (selectedFundReturn == null || selectedFundReturn.isNull()) {
                throw new ExternalApiException("Modern Portfolio average return for selected ticker is missing");
            }

            return selectedFundReturn.asDouble();
        } catch (ExternalApiException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new ExternalApiException("Unable to parse historical return response", ex);
        }
    }
}