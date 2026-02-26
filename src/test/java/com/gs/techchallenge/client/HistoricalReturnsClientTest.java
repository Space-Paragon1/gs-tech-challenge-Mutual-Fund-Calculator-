package com.gs.techchallenge.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gs.techchallenge.exception.ExternalApiException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestClient;

import java.net.URI;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.within;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class HistoricalReturnsClientTest {

    private HistoricalReturnsClient client;
    private RestClient restClient;

    @BeforeEach
    void setUp() {
        restClient = mock(RestClient.class);
        client = new HistoricalReturnsClient(new ObjectMapper());
        ReflectionTestUtils.setField(client, "restClient", restClient);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private void stubResponse(String json) {
        RestClient.RequestHeadersUriSpec requestSpec = mock(RestClient.RequestHeadersUriSpec.class);
        RestClient.ResponseSpec responseSpec = mock(RestClient.ResponseSpec.class);
        when(restClient.get()).thenReturn(requestSpec);
        when(requestSpec.uri(any(URI.class))).thenReturn(requestSpec);
        when(requestSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.body(String.class)).thenReturn(json);
    }

    @Test
    void returnsAnnualizedReturnFromValidResponse() {
        // monthly return of 0.01 => annualized = (1.01)^12 - 1
        stubResponse("{\"averageReturns\": [0.01, 0.009]}");

        double result = client.getExpectedAnnualSp500ReturnFromFiveYears("VFIAX");

        assertThat(result).isCloseTo(Math.pow(1.01, 12) - 1, within(1e-9));
    }

    @Test
    void annualizesMonthlyReturnUsingCompoundFormula() {
        double monthlyReturn = 0.008;
        stubResponse("{\"averageReturns\": [" + monthlyReturn + "]}");

        double result = client.getExpectedAnnualSp500ReturnFromFiveYears("FXAIX");

        assertThat(result).isCloseTo(Math.pow(1 + monthlyReturn, 12) - 1, within(1e-9));
    }

    @Test
    void throwsOnEmptyResponseBody() {
        stubResponse("");
        assertThatThrownBy(() -> client.getExpectedAnnualSp500ReturnFromFiveYears("VFIAX"))
                .isInstanceOf(ExternalApiException.class);
    }

    @Test
    void throwsOnNullResponseBody() {
        stubResponse(null);
        assertThatThrownBy(() -> client.getExpectedAnnualSp500ReturnFromFiveYears("VFIAX"))
                .isInstanceOf(ExternalApiException.class);
    }

    @Test
    void throwsWhenApiReturnsNon200Status() {
        stubResponse("{\"status\": \"400\", \"data\": \"Bad ticker parameter\"}");
        assertThatThrownBy(() -> client.getExpectedAnnualSp500ReturnFromFiveYears("VFIAX"))
                .isInstanceOf(ExternalApiException.class);
    }

    @Test
    void throwsWhenAverageReturnsMissing() {
        stubResponse("{\"someOtherField\": 1.0}");
        assertThatThrownBy(() -> client.getExpectedAnnualSp500ReturnFromFiveYears("VFIAX"))
                .isInstanceOf(ExternalApiException.class);
    }

    @Test
    void throwsWhenAverageReturnsIsEmptyArray() {
        stubResponse("{\"averageReturns\": []}");
        assertThatThrownBy(() -> client.getExpectedAnnualSp500ReturnFromFiveYears("VFIAX"))
                .isInstanceOf(ExternalApiException.class);
    }

    @Test
    void throwsWhenSp500ReturnIsNullInArray() {
        stubResponse("{\"averageReturns\": [null, 0.01]}");
        assertThatThrownBy(() -> client.getExpectedAnnualSp500ReturnFromFiveYears("VFIAX"))
                .isInstanceOf(ExternalApiException.class);
    }

    @Test
    @SuppressWarnings({"unchecked", "rawtypes"})
    void throwsWhenRestClientFails() {
        RestClient.RequestHeadersUriSpec requestSpec = mock(RestClient.RequestHeadersUriSpec.class);
        RestClient.ResponseSpec responseSpec = mock(RestClient.ResponseSpec.class);
        when(restClient.get()).thenReturn(requestSpec);
        when(requestSpec.uri(any(URI.class))).thenReturn(requestSpec);
        when(requestSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.body(String.class)).thenThrow(new RuntimeException("network timeout"));

        assertThatThrownBy(() -> client.getExpectedAnnualSp500ReturnFromFiveYears("VFIAX"))
                .isInstanceOf(ExternalApiException.class)
                .hasMessageContaining("5-year S&P historical return");
    }

    @Test
    void throwsOnMalformedJson() {
        stubResponse("not-json{{{");
        assertThatThrownBy(() -> client.getExpectedAnnualSp500ReturnFromFiveYears("VFIAX"))
                .isInstanceOf(ExternalApiException.class);
    }

    @Test
    void usesFirstElementOfAverageReturnsAsSp500Return() {
        // First element is S&P 500, second is the fund — only the first should be used
        stubResponse("{\"averageReturns\": [0.005, 0.020]}");

        double result = client.getExpectedAnnualSp500ReturnFromFiveYears("VTSAX");

        assertThat(result).isCloseTo(Math.pow(1.005, 12) - 1, within(1e-9));
    }
}
