package com.nikhil.project.uber.uberApp.services.impl;

import com.nikhil.project.uber.uberApp.entities.Driver;
import com.nikhil.project.uber.uberApp.entities.Ride;
import com.nikhil.project.uber.uberApp.entities.RideRequest;
import com.nikhil.project.uber.uberApp.entities.Rider;
import com.nikhil.project.uber.uberApp.enums.RideRequestStatus;
import com.nikhil.project.uber.uberApp.enums.RideStatus;
import com.nikhil.project.uber.uberApp.exceptions.RideNotFoundException;
import com.nikhil.project.uber.uberApp.repositories.DriverRepository;
import com.nikhil.project.uber.uberApp.repositories.RideRepository;
import com.nikhil.project.uber.uberApp.services.RideRequestService;
import com.nikhil.project.uber.uberApp.services.RideService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;

@Service
@RequiredArgsConstructor
public class RideServiceImpl implements RideService {

    private final RideRepository rideRepository;
    private final RideRequestService rideRequestService;
    private final ModelMapper modelMapper;
    private final DriverRepository driverRepository;

    @Override
    public Ride getRideById(Long rideId) {
        return rideRepository.findById(rideId).orElseThrow(() -> new RideNotFoundException(
                "Ride not found with id: " + rideId
        ));
    }

    @Override
    @Transactional
    public Ride createNewRide(RideRequest rideRequest, Driver driver) {
        rideRequest.setRideRequestStatus(RideRequestStatus.CONFIRMED);

        // Map RideRequest → Ride. ModelMapper may copy ID-like fields unintentionally.
        Ride ride = modelMapper.map(rideRequest, Ride.class);

        ride.setRideStatus(RideStatus.CONFIRMED);
        ride.setDriver(driver);
        ride.setOtp(generateOtp());

        /*
         * Explicitly set the ID to null to ensure Hibernate treats this Ride as a new entity.
         * If ModelMapper mapped an ID value from RideRequest, Hibernate would attempt to
         * UPDATE an existing record instead of inserting a new one. Setting the ID to null
         * guarantees an INSERT operation.
         */
        ride.setId(null);

        rideRequestService.updateRideRequest(rideRequest);
        return rideRepository.save(ride);
    }


    @Override
    public Ride updateRideStatus(Ride ride, RideStatus rideStatus) {
        ride.setRideStatus(rideStatus);
        return rideRepository.save(ride);
    }

    @Override
    public Page<Ride> getAllRidesOfRider(Rider rider, PageRequest pageRequest) {
        return rideRepository.findByRider(rider,pageRequest);
    }

    @Override
    public Page<Ride> getAllRidesOfDriver(Driver driver, PageRequest pageRequest) {
        return driverRepository.findByDriver(driver,pageRequest);
    }

    public String generateOtp() {
        SecureRandom random = new SecureRandom();
        int otp = 100000 + random.nextInt(900000);
        return String.valueOf(otp);
    }

}
