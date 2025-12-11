package com.nikhil.project.uber.uberApp.services;

import com.nikhil.project.uber.uberApp.entities.RideRequest;

public interface RideRequestService {
    RideRequest findRideRequestById(Long requestId);

    void updateRideRequest(RideRequest rideRequest);
}
