package com.nikhil.project.uber.uberApp.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PointDto {
    private Double[] coordinates;
    private String type = "point";

    public PointDto(Double[] coordinates) {
        this.coordinates = coordinates;
    }
}
