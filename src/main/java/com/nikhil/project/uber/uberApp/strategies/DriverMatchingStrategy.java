package com.nikhil.project.uber.uberApp.strategies;

import com.nikhil.project.uber.uberApp.dto.RideRequestDto;
import com.nikhil.project.uber.uberApp.entities.Driver;

import java.util.List;

public interface DriverMatchingStrategy {
    List<Driver> findMatchingDriver(RideRequestDto rideRequestDto);
}
