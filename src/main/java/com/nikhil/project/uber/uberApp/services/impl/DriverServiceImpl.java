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
import com.nikhil.project.uber.uberApp.services.PaymentService;
import com.nikhil.project.uber.uberApp.services.RideRequestService;
import com.nikhil.project.uber.uberApp.services.RideService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class DriverServiceImpl implements DriverService {

    private final RideRequestService rideRequestService;
    private final DriverRepository driverRepository;
    private final RideService rideService;
    private final ModelMapper modelMapper;
    private final PaymentService paymentService;

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

        updateDriverAvailability(currentDriver, false);

        Ride newRide = rideService.createNewRide(rideRequest, currentDriver);

        return modelMapper.map(newRide,RideDto.class);
    }

    @Override
    @Transactional
    public RideDto cancelRide(Long rideId) {
        Ride ride = rideService.getRideById(rideId);
        Driver currentDriver = getCurrentDriver();
        if (!currentDriver.equals(ride.getDriver())) {
            throw new DriverCouldNotAcceptException(
                    "Driver cannot start a ride as he has not accepted it earlier"
            );
        }

        if (ride.getRideStatus().equals(RideStatus.CONFIRMED)){
            throw new RideCanNotBeCancelledException(
                    "Ride cannot be cancelled, invalid status: " + ride.getRideStatus()
            );
        }

        rideService.updateRideStatus(ride,RideStatus.CANCELLED);
        updateDriverAvailability(currentDriver, true);
        driverRepository.save(currentDriver);
        return modelMapper.map(ride,RideDto.class);
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
        paymentService.createNewPayment(saveRide);

        return modelMapper.map(saveRide,RideDto.class);
    }

    @Override
    @Transactional
    public RideDto endRide(Long rideId) {
        Ride ride = rideService.getRideById(rideId);
        Driver currentDriver = getCurrentDriver();
        if (!currentDriver.equals(ride.getDriver())) {
            throw new DriverCouldNotAcceptException(
                    "Driver cannot start a ride as he has not accepted it earlier"
            );
        }

        if (!ride.getRideStatus().equals(RideStatus.ONGOING)){
            throw new RideStatusNotConfirmedException(
                    "Ride status is not ONGOING hence cannot be ended, status: " + ride.getRideStatus()
            );
        }

        ride.setEndedAt(LocalDateTime.now());
        Ride savedRide = rideService.updateRideStatus(ride, RideStatus.ENDED);
        updateDriverAvailability(currentDriver, true);

        paymentService.processPayment(ride);
        return modelMapper.map(savedRide,RideDto.class);
    }

    @Override
    public RiderDto rateRider(Long rideId, Integer rating) {
        return null;
    }

    @Override
    public DriverDto getMyProfile() {
        return modelMapper.map(getCurrentDriver(),DriverDto.class);
    }

    @Override
    @Transactional
    public Page<RideDto> getAllMyRides(PageRequest pageRequest) {
        Driver currentDriver = getCurrentDriver();
        return rideService.getAllRidesOfDriver(currentDriver, pageRequest).map(
                ride -> modelMapper.map(ride, RideDto.class)
        );
    }

    @Override
    public Driver getCurrentDriver() {
        return driverRepository.findById(2L).orElseThrow(() ->
                    new DriverNotFoundException("Driver not found with id " + 2)
                );
    }

    @Override
    public Driver updateDriverAvailability(Driver driver, boolean available) {
        driver.setAvailable(available);
        driverRepository.save(driver);
        return driver;
    }

    @Override
    public Driver createNewDriver(Driver driver) {
        return driverRepository.save(driver);
    }
}
