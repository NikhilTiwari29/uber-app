package com.nikhil.project.uber.uberApp.services.impl;

import com.nikhil.project.uber.uberApp.dto.DriverDto;
import com.nikhil.project.uber.uberApp.dto.RideDto;
import com.nikhil.project.uber.uberApp.dto.RiderDto;
import com.nikhil.project.uber.uberApp.entities.Driver;
import com.nikhil.project.uber.uberApp.entities.Ride;
import com.nikhil.project.uber.uberApp.entities.RideRequest;
import com.nikhil.project.uber.uberApp.enums.RideRequestStatus;
import com.nikhil.project.uber.uberApp.enums.RideStatus;
import com.nikhil.project.uber.uberApp.exceptions.*;
import com.nikhil.project.uber.uberApp.repositories.DriverRepository;
import com.nikhil.project.uber.uberApp.services.DriverService;
import com.nikhil.project.uber.uberApp.services.RideRequestService;
import com.nikhil.project.uber.uberApp.services.RideService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DriverServiceImpl implements DriverService {

    private final RideRequestService rideRequestService;
    private final DriverRepository driverRepository;
    private final RideService rideService;
    private final ModelMapper modelMapper;

    @Override
    @Transactional
    public RideDto acceptRide(Long rideRequestId) {
        RideRequest rideRequest = rideRequestService.findRideRequestById(rideRequestId);
        if (!rideRequest.getRideRequestStatus().equals(RideRequestStatus.PENDING)) {
            throw new RideRequestCannotBeAcceptedException(
                    "Ride request cannot be accepted, status is " + rideRequest.getRideRequestStatus()
            );
        }

        Driver currentDriver = getCurrentDriver();
        if (!currentDriver.getAvailable()){
            throw new DriverNotAvailableException("Driver cannot accept ride due to unavailability");
        }

        currentDriver.setAvailable(false);
        driverRepository.save(currentDriver);

        Ride newRide = rideService.createNewRide(rideRequest, currentDriver);

        return modelMapper.map(newRide,RideDto.class);
    }

    @Override
    public RideDto cancelRide(Long rideId) {
        return null;
    }

    @Override
    @Transactional
    public RideDto startRide(Long rideId,String otp) {
        Ride ride = rideService.getRideById(rideId);
        Driver currentDriver = getCurrentDriver();
        if (!currentDriver.equals(ride.getDriver())) {
            throw new DriverCouldNotAcceptException(
                    "Driver cannot start a ride as he has not accepted it earlier"
            );
        }

        if (!ride.getRideStatus().equals(RideStatus.CONFIRMED)){
            throw new RideStatusNotConfirmedException(
                    "Ride status is not confirmed hence cannot be started, status: " + ride.getRideStatus()
            );
        }

        if (!otp.equals(ride.getOtp())){
            throw new InValidOtpException(
                    "Otp is not valid, OTP: " + otp
            );
        }

        ride.setStartedAt(LocalDateTime.now());
        Ride saveRide = rideService.updateRideStatus(ride, RideStatus.ONGOING);
        return modelMapper.map(saveRide,RideDto.class);
    }

    @Override
    public RideDto endRide(Long rideId) {
        return null;
    }

    @Override
    public RiderDto rateRider(Long rideId, Integer rating) {
        return null;
    }

    @Override
    public DriverDto getMyProfile() {
        return null;
    }

    @Override
    public List<RideDto> getAllMyRides() {
        return List.of();
    }

    @Override
    public Driver getCurrentDriver() {
        return driverRepository.findById(2L).orElseThrow(() ->
                    new DriverNotFoundException("Driver not found with id " + 2)
                );
    }
}
