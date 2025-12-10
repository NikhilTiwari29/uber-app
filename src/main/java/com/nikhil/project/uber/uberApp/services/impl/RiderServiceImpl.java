package com.nikhil.project.uber.uberApp.services.impl;

import com.nikhil.project.uber.uberApp.dto.DriverDto;
import com.nikhil.project.uber.uberApp.dto.RideDto;
import com.nikhil.project.uber.uberApp.dto.RideRequestDto;
import com.nikhil.project.uber.uberApp.dto.RiderDto;
import com.nikhil.project.uber.uberApp.entities.*;
import com.nikhil.project.uber.uberApp.enums.RideRequestStatus;
import com.nikhil.project.uber.uberApp.exceptions.RiderNotFoundException;
import com.nikhil.project.uber.uberApp.repositories.RideRequestRepository;
import com.nikhil.project.uber.uberApp.repositories.RiderRepository;
import com.nikhil.project.uber.uberApp.services.RiderService;
import com.nikhil.project.uber.uberApp.strategies.DriverMatchingStrategy;
import com.nikhil.project.uber.uberApp.strategies.RideFareCalculationStrategy;
import com.nikhil.project.uber.uberApp.strategies.RideStrategyManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class RiderServiceImpl implements RiderService {

    private final ModelMapper modelMapper;
    private final RideStrategyManager rideStrategyManager;
    private final RideRequestRepository rideRequestRepository;
    private final RiderRepository riderRepository;

    @Override
    @Transactional
    public RideRequestDto requestRide(RideRequestDto rideRequestDto) {

        // 1️⃣ Get current rider (from SecurityContext / token / session)
        Rider rider = getCurrentRider();

        // 2️⃣ Map DTO → Entity
        RideRequest rideRequest = modelMapper.map(rideRequestDto, RideRequest.class);
        rideRequest.setRideRequestStatus(RideRequestStatus.PENDING);
        // 3️⃣ Set the rider on the rideRequest as this will set the foreign key
        rideRequest.setRider(rider);

        // 4️⃣ Calculate fare
        Double fare = rideStrategyManager
                .rideFareCalculationStrategy()
                .calculateFare(rideRequest);
        rideRequest.setFare(fare);

        // 5️⃣ Save to DB
        RideRequest savedRideRequest = rideRequestRepository.save(rideRequest);

        // 6️⃣ Use strategy for driver matching
        rideStrategyManager
                .driverMatchingStrategy(rider.getRating())
                .findMatchingDrivers(savedRideRequest);

        // 7️⃣ Map back to DTO
        return modelMapper.map(savedRideRequest, RideRequestDto.class);
    }


    @Override
    public RideDto cancelRide(Long rideId) {
        return null;
    }

    @Override
    public RideDto endRide(Long rideId) {
        return null;
    }

    @Override
    public DriverDto rateDriver(Long rideId, Integer rating) {
        return null;
    }

    @Override
    public RiderDto getMyProfile() {
        return null;
    }

    @Override
    public List<RideDto> getAllMyRides() {
        return List.of();
    }

    @Override
    public Rider createNewRider(User user) {
        Rider rider = new Rider();
        rider.setUser(user);
        rider.setRating(0.0);

        return riderRepository.save(rider);
    }

    @Override
    public Rider getCurrentRider() {
        return riderRepository.findById(1L).orElseThrow( () ->
                new RiderNotFoundException(
                        "Rider not found with id: " + 1L
                ));
    }
}
