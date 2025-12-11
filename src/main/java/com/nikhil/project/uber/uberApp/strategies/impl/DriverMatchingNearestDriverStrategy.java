package com.nikhil.project.uber.uberApp.strategies.impl;

import com.nikhil.project.uber.uberApp.entities.Driver;
import com.nikhil.project.uber.uberApp.entities.RideRequest;
import com.nikhil.project.uber.uberApp.repositories.DriverRepository;
import com.nikhil.project.uber.uberApp.strategies.DriverMatchingStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DriverMatchingNearestDriverStrategy implements DriverMatchingStrategy {

    private final DriverRepository driverRepository;

    @Override
    public List<Driver> findMatchingDrivers(RideRequest rideRequest) {
        List<Driver> findTenNearestDrivers =
                driverRepository.findTenNearestDrivers(rideRequest.getPickupLocation());
        return List.of();
    }
}
