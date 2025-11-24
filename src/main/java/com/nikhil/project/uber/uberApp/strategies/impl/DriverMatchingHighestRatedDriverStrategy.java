package com.nikhil.project.uber.uberApp.strategies.impl;

import com.nikhil.project.uber.uberApp.dto.RideRequestDto;
import com.nikhil.project.uber.uberApp.entities.Driver;
import com.nikhil.project.uber.uberApp.strategies.DriverMatchingStrategy;

import java.util.List;

public class DriverMatchingHighestRatedDriverStrategy implements DriverMatchingStrategy {
    @Override
    public List<Driver> findMatchingDriver(RideRequestDto rideRequestDto) {
        return List.of();
    }
}
