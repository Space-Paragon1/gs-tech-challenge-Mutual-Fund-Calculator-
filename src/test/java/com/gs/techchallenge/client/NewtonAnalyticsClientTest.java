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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class NewtonAnalyticsClientTest {

    private NewtonAnalyticsClient client;
    private RestClient restClient;

    @BeforeEach
    void setUp() {
        restClient = mock(RestClient.class);
        client = new NewtonAnalyticsClient(new ObjectMapper());
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
    void getBetaFromTopLevelBetaField() {
        stubResponse("{\"beta\": 1.23}");
        assertThat(client.getBeta("VFIAX")).isEqualTo(1.23);
    }

    @Test
    void getBetaFromDataFieldDirectly() {
        stubResponse("{\"data\": 0.95}");
        assertThat(client.getBeta("FXAIX")).isEqualTo(0.95);
    }

    @Test
    void getBetaFromNestedDataBetaField() {
        stubResponse("{\"data\": {\"beta\": 1.10}}");
        assertThat(client.getBeta("SWPPX")).isEqualTo(1.10);
    }

    @Test
    void getBetaFromResultArrayField() {
        stubResponse("{\"result\": [{\"beta\": 0.88}]}");
        assertThat(client.getBeta("VTSAX")).isEqualTo(0.88);
    }

    @Test
    void getBetaThrowsOnEmptyResponse() {
        stubResponse("");
        assertThatThrownBy(() -> client.getBeta("VFIAX"))
                .isInstanceOf(ExternalApiException.class);
    }

    @Test
    void getBetaThrowsOnNullResponse() {
        stubResponse(null);
        assertThatThrownBy(() -> client.getBeta("VFIAX"))
                .isInstanceOf(ExternalApiException.class);
    }

    @Test
    void getBetaThrowsWhenNoBetaFieldFound() {
        stubResponse("{\"someOtherField\": 1.0}");
        assertThatThrownBy(() -> client.getBeta("VFIAX"))
                .isInstanceOf(ExternalApiException.class)
                .hasMessageContaining("Unable to fetch beta");
    }

    @Test
    @SuppressWarnings({"unchecked", "rawtypes"})
    void getBetaThrowsWhenRestClientFails() {
        RestClient.RequestHeadersUriSpec requestSpec = mock(RestClient.RequestHeadersUriSpec.class);
        RestClient.ResponseSpec responseSpec = mock(RestClient.ResponseSpec.class);
        when(restClient.get()).thenReturn(requestSpec);
        when(requestSpec.uri(any(URI.class))).thenReturn(requestSpec);
        when(requestSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.body(String.class)).thenThrow(new RuntimeException("connection refused"));

        assertThatThrownBy(() -> client.getBeta("VFIAX"))
                .isInstanceOf(ExternalApiException.class)
                .hasMessageContaining("Unable to fetch beta");
    }

    @Test
    void getBetaThrowsOnMalformedJson() {
        stubResponse("not-valid-json{{{");
        assertThatThrownBy(() -> client.getBeta("VFIAX"))
                .isInstanceOf(ExternalApiException.class);
    }

    @Test
    void getBetaFromDataFieldAsText() {
        stubResponse("{\"data\": \"1.15\"}");
        assertThat(client.getBeta("VIGAX")).isEqualTo(1.15);
    }
}
