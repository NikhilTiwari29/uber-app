package com.nikhil.project.uber.uberApp.services.impl;

import com.nikhil.project.uber.uberApp.dto.DriverDto;
import com.nikhil.project.uber.uberApp.dto.RideDto;
import com.nikhil.project.uber.uberApp.dto.RideRequestDto;
import com.nikhil.project.uber.uberApp.dto.RiderDto;
import com.nikhil.project.uber.uberApp.entities.Ride;
import com.nikhil.project.uber.uberApp.entities.RideRequest;
import com.nikhil.project.uber.uberApp.entities.Rider;
import com.nikhil.project.uber.uberApp.entities.User;
import com.nikhil.project.uber.uberApp.enums.RideRequestStatus;
import com.nikhil.project.uber.uberApp.enums.RideStatus;
import com.nikhil.project.uber.uberApp.exceptions.RideCanNotBeCancelledException;
import com.nikhil.project.uber.uberApp.exceptions.RiderNotFoundException;
import com.nikhil.project.uber.uberApp.repositories.RideRequestRepository;
import com.nikhil.project.uber.uberApp.repositories.RiderRepository;
import com.nikhil.project.uber.uberApp.services.DriverService;
import com.nikhil.project.uber.uberApp.services.RideService;
import com.nikhil.project.uber.uberApp.services.RiderService;
import com.nikhil.project.uber.uberApp.strategies.RideStrategyManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class RiderServiceImpl implements RiderService {

    private final ModelMapper modelMapper;
    private final RideStrategyManager rideStrategyManager;
    private final RideRequestRepository rideRequestRepository;
    private final RiderRepository riderRepository;
    private final RideService rideService;
    private final DriverService driverService;

    @Override
    @Transactional
    public RideRequestDto requestRide(RideRequestDto rideRequestDto) {

        Rider rider = getCurrentRider();

        RideRequest rideRequest = modelMapper.map(rideRequestDto, RideRequest.class);
        rideRequest.setRideRequestStatus(RideRequestStatus.PENDING);

        rideRequest.setRider(rider);

        // Calculate fare
        Double fare = rideStrategyManager
                .rideFareCalculationStrategy()
                .calculateFare(rideRequest);

        rideRequest.setFare(fare);

        RideRequest savedRideRequest = rideRequestRepository.save(rideRequest);

        // strategy for driver matching
        rideStrategyManager
                .driverMatchingStrategy(rider.getRating())
                .findMatchingDrivers(savedRideRequest);

        return modelMapper.map(savedRideRequest, RideRequestDto.class);
    }


    @Override
    public RideDto cancelRide(Long rideId) {
        Rider currentRider = getCurrentRider();
        Ride ride = rideService.getRideById(rideId);
        if (!currentRider.equals(ride.getRider())){
            throw new RuntimeException("Rider doesn't own this ride with id: " + rideId);
        }

        if (ride.getRideStatus().equals(RideStatus.CONFIRMED)){
            throw new RideCanNotBeCancelledException(
                    "Ride cannot be cancelled, invalid status: " + ride.getRideStatus()
            );
        }

        Ride savedRide = rideService.updateRideStatus(ride, RideStatus.CANCELLED);
        driverService.updateDriverAvailability(ride.getDriver(), true);

        return modelMapper.map(savedRide, RideDto.class);
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
        Rider currentRider = getCurrentRider();
        return modelMapper.map(currentRider, RiderDto.class);
    }

    @Override
    public Page<RideDto> getAllMyRides(PageRequest pageRequest) {
        Rider currentRider = getCurrentRider();
        return rideService.getAllRidesOfRider(currentRider, pageRequest).map(
                ride -> modelMapper.map(ride, RideDto.class)
        );
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
