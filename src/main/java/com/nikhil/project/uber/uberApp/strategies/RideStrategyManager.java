package com.nikhil.project.uber.uberApp.strategies;

import com.nikhil.project.uber.uberApp.strategies.impl.DriverMatchingHighestRatedDriverStrategy;
import com.nikhil.project.uber.uberApp.strategies.impl.DriverMatchingNearestDriverStrategy;
import com.nikhil.project.uber.uberApp.strategies.impl.RideFareDefaultFarreCalculationStrategy;
import com.nikhil.project.uber.uberApp.strategies.impl.RideFareSurgePricingFareCalculationStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalTime;

@Component
@RequiredArgsConstructor
public class RideStrategyManager {

    private final DriverMatchingHighestRatedDriverStrategy highestRatedDriverStrategy;
    private final DriverMatchingNearestDriverStrategy nearestDriverStrategy;
    private final RideFareDefaultFarreCalculationStrategy defaultFarreCalculationStrategy;
    private final RideFareSurgePricingFareCalculationStrategy surgePricingFareCalculationStrategy;

    public DriverMatchingStrategy driverMatchingStrategy(double riderRating){
        if (riderRating >= 4.8){
            return highestRatedDriverStrategy;
        }
        else {
            return nearestDriverStrategy;
        }
    }

    public RideFareCalculationStrategy rideFareCalculationStrategy(){
        LocalTime surgeStartTime = LocalTime.of(18,0);
        LocalTime surgeEndTime = LocalTime.of(21,0);
        LocalTime currentTime = LocalTime.now();

        boolean isSurgeTime = currentTime.isAfter(surgeStartTime) && currentTime.isBefore(surgeEndTime);

        if (isSurgeTime){
            return surgePricingFareCalculationStrategy;
        }
        else {
            return defaultFarreCalculationStrategy;
        }
    }
}
