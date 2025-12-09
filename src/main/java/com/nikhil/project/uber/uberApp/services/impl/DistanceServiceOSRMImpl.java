package com.nikhil.project.uber.uberApp.services.impl;

import com.nikhil.project.uber.uberApp.services.DistanceService;
import lombok.Data;
import org.locationtech.jts.geom.Point;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;

@Service
public class DistanceServiceOSRMImpl implements DistanceService {

    private static final String OSRM_API_BASE_URL =
            "http://router.project-osrm.org/route/v1/driving/";

    @Override
    public double calculateDistance(Point src, Point dest) {
        String uri = src.getX() + "," + src.getY() + ";" + dest.getX() + "," + dest.getY();
        try {
            OSRMResponseDto osrmResponseDto = RestClient.builder()
                    .baseUrl(OSRM_API_BASE_URL)
                    .build()
                    .get()
                    .uri(uri)
                    .retrieve()
                    .body(OSRMResponseDto.class);

            if (osrmResponseDto == null ||
                    osrmResponseDto.getRoutes() == null ||
                    osrmResponseDto.getRoutes().isEmpty()) {
                throw new RuntimeException("Empty response received from OSRM");
            }

            // ✅ OSRM returns distance in METERS → convert to KM
            return osrmResponseDto.getRoutes().get(0).getDistance() / 1000.0;

        } catch (Exception ex) {
            throw new RuntimeException("Error while calling OSRM API for distance calculation", ex);
        }
    }
}

@Data
class OSRMResponseDto {
    private List<OSRMRoute> routes;
}

@Data
class OSRMRoute {
    private Double distance;
}
