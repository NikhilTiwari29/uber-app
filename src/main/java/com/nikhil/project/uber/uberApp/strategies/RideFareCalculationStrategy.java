package com.nikhil.project.uber.uberApp.strategies;

import com.nikhil.project.uber.uberApp.dto.RideRequestDto;

public interface RideFareCalculationStrategy {
    double calculateFare(RideRequestDto rideRequestDto);
}
