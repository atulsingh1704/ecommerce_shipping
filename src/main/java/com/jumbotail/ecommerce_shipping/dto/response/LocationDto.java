package com.jumbotail.ecommerce_shipping.dto.response;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for returning geographic coordinates in API responses.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LocationDto {
    private double lat;
    private double lng;
}