package com.nikhil.project.uber.uberApp.services.impl;

import com.nikhil.project.uber.uberApp.entities.RideRequest;
import com.nikhil.project.uber.uberApp.exceptions.RideRequestNotFoundException;
import com.nikhil.project.uber.uberApp.repositories.RideRequestRepository;
import com.nikhil.project.uber.uberApp.services.RideRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RideRequestServiceImpl implements RideRequestService {

    private final RideRequestRepository rideRequestRepository;

    @Override
    public RideRequest findRideRequestById(Long requestId) {
        return rideRequestRepository.findById(requestId).orElseThrow(() ->
                new RideRequestNotFoundException("Ride request not found with id: " + requestId));
    }

    @Override
    public void updateRideRequest(RideRequest rideRequest) {
        rideRequestRepository.findById(rideRequest.getId()).orElseThrow(() -> {
            throw new RideRequestNotFoundException("Ride request not found with id " + rideRequest.getId());
        });
        rideRequestRepository.save(rideRequest);
    }
}
