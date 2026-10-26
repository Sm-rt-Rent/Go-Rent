package com.Vhytor.GoRent.dtos.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public class CreateHomeRequest {

    @NotBlank(message = "Address is required")
    private String address;

    @NotBlank(message = "Description is required")
    private String description;

    @NotNull(message = "Price per month is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
    private BigDecimal pricePerMonth;

    @NotNull(message = "Viewing fee is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Viewing fee must be greater than 0")
    private BigDecimal viewingFee;

    @NotNull(message = "Latitude is required")
    private Double latitude;

    @NotNull(message = "Longitude is required")
    private Double longitude;

    @NotNull(message = "Bedrooms is required")
    private Integer bedrooms;

    @NotNull(message = "Bathrooms is required")
    private Integer bathrooms;

    @NotNull(message = "Square feet is required")
    private Integer squareFeet;

    @NotBlank(message = "Pet policy is required")
    private String petsAllowed;

    @NotBlank(message = "Property details are required")
    private String propertyDetails;

    // Getters and Setters
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public BigDecimal getPricePerMonth() { return pricePerMonth; }
    public void setPricePerMonth(BigDecimal pricePerMonth) { this.pricePerMonth = pricePerMonth; }

    public BigDecimal getViewingFee() { return viewingFee; }
    public void setViewingFee(BigDecimal viewingFee) { this.viewingFee = viewingFee; }

    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }

    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }

    // Getters and setters
    public Integer getBedrooms() { return bedrooms; }
    public void setBedrooms(Integer bedrooms) { this.bedrooms = bedrooms; }

    public Integer getBathrooms() { return bathrooms; }
    public void setBathrooms(Integer bathrooms) { this.bathrooms = bathrooms; }

    public Integer getSquareFeet() { return squareFeet; }
    public void setSquareFeet(Integer squareFeet) { this.squareFeet = squareFeet; }

    public String getPetsAllowed() { return petsAllowed; }
    public void setPetsAllowed(String petsAllowed) { this.petsAllowed = petsAllowed; }

    public String getPropertyDetails() { return propertyDetails; }
    public void setPropertyDetails(String propertyDetails) { this.propertyDetails = propertyDetails; }
}
