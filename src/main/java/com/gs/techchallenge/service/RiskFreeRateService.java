package com.gs.techchallenge.service;

import org.springframework.stereotype.Service;

@Service
public class RiskFreeRateService {

    private static final double RISK_FREE_RATE = 0.0425;

    public double getRiskFreeRate() {
        return RISK_FREE_RATE;
    }
}