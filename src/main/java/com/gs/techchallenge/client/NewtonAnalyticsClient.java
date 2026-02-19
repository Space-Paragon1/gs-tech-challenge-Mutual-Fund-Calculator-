package com.gs.techchallenge.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gs.techchallenge.exception.ExternalApiException;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@Component
public class NewtonAnalyticsClient {

    private static final String BASE_URL = "https://api.newtonanalytics.com/stock-beta/";

    private final RestClient restClient;
    private final ObjectMapper objectMapper;

    public NewtonAnalyticsClient(ObjectMapper objectMapper) {
        this.restClient = RestClient.create();
        this.objectMapper = objectMapper;
    }

    public double getBeta(String ticker) {
        try {
            URI uri = UriComponentsBuilder.fromHttpUrl(BASE_URL)
                .queryParam("ticker", ticker)
                .queryParam("index", "^GSPC")
                .queryParam("interval", "1mo")
                .queryParam("observations", "12")
                .encode()
                .build()
                .toUri();

            String responseBody = restClient.get()
                    .uri(uri)
                    .retrieve()
                    .body(String.class);
            return parseBeta(responseBody);
        } catch (Exception ex) {
            throw new ExternalApiException("Unable to fetch beta from Newton Analytics", ex);
        }
    }

    private double parseBeta(String responseBody) {
        if (responseBody == null || responseBody.isBlank()) {
            throw new ExternalApiException("Newton Analytics returned an empty beta response");
        }

        try {
            JsonNode root = objectMapper.readTree(responseBody);
            JsonNode betaNode = findBetaNode(root);
            if (betaNode == null || betaNode.isNull()) {
                throw new ExternalApiException("Newton Analytics response did not include beta");
            }
            return Double.parseDouble(betaNode.asText());
        } catch (ExternalApiException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new ExternalApiException("Unable to parse beta from Newton Analytics response", ex);
        }
    }

    private JsonNode findBetaNode(JsonNode root) {
        if (root.has("beta")) {
            return root.get("beta");
        }
        if (root.has("data")) {
            JsonNode dataNode = root.get("data");
            if (dataNode.isNumber() || dataNode.isTextual()) {
                return dataNode;
            }
            if (dataNode.has("beta")) {
                return dataNode.get("beta");
            }
        }
        if (root.has("result") && root.get("result").isArray() && !root.get("result").isEmpty()) {
            JsonNode first = root.get("result").get(0);
            if (first.has("beta")) {
                return first.get("beta");
            }
        }
        return null;
    }
}