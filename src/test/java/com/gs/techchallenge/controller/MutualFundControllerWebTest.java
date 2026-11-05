package com.gs.techchallenge.controller;

import com.gs.techchallenge.service.InvestmentProjectionService;
import com.gs.techchallenge.service.MutualFundCatalogService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Sends real HTTP requests through Spring MVC to check validation and
 * error responses end to end (controller + GlobalExceptionHandler).
 */
@WebMvcTest(MutualFundController.class)
class MutualFundControllerWebTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MutualFundCatalogService mutualFundCatalogService;

    @MockitoBean
    private InvestmentProjectionService investmentProjectionService;

    @Test
    void missingParameterReturns400() throws Exception {
        mockMvc.perform(get("/api/investments/future-value")
                        .param("ticker", "VFIAX")
                        .param("years", "5"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Missing required parameter: principal"));
    }

    @Test
    void yearsAboveFiftyReturns400() throws Exception {
        when(mutualFundCatalogService.isSupported("VFIAX")).thenReturn(true);

        mockMvc.perform(get("/api/investments/future-value")
                        .param("ticker", "VFIAX")
                        .param("principal", "10000")
                        .param("years", "51"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Invalid request parameters"));
    }

    @Test
    void negativePrincipalReturns400() throws Exception {
        mockMvc.perform(get("/api/investments/future-value")
                        .param("ticker", "VFIAX")
                        .param("principal", "-5")
                        .param("years", "5"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Invalid request parameters"));
    }

    @Test
    void unknownPathReturns404() throws Exception {
        mockMvc.perform(get("/some-page-that-does-not-exist"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not found"));
    }
}
