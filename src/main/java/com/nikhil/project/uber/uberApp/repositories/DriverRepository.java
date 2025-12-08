package com.nikhil.project.uber.uberApp.repositories;

import com.nikhil.project.uber.uberApp.entities.Driver;
import org.locationtech.jts.geom.Point;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DriverRepository extends JpaRepository<Driver, Long> {

    @Query(
            value = """
            SELECT d.*
            FROM driver d
            WHERE d.available = true
              AND ST_DWithin(d.current_location, :pickupLocation, 10000)
            ORDER BY ST_Distance(d.current_location, :pickupLocation)
            LIMIT 10
            """,
            nativeQuery = true
    )
    List<Driver> findTenNearestDrivers(@Param("pickupLocation") Point pickupLocation);
}
