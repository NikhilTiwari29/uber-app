package com.nikhil.project.uber.uberApp.strategies.impl;

import com.nikhil.project.uber.uberApp.dto.RideRequestDto;
import com.nikhil.project.uber.uberApp.entities.RideRequest;
import com.nikhil.project.uber.uberApp.strategies.RideFareCalculationStrategy;

public class RideFareCalculationStrategyImpl implements RideFareCalculationStrategy {
    @Override
    public double calculateFare(RideRequest rideRequestDto) {
        return 0;
    }
}
