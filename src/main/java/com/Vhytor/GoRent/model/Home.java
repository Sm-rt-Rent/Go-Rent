package com.Vhytor.GoRent.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "homes")
@AllArgsConstructor
@NoArgsConstructor
public class Home {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "home_id")
    private Long homeId;

    @ManyToOne
    @JoinColumn(name = "landlord_id", nullable = false)
    private User landlord; // This connects the house to a specific Landlord

    private String address;
    private String description;
    private BigDecimal pricePerMonth;
    private BigDecimal viewingFee; // The "little token" debited to view

    private String smartLockCode; // The current code to the door

    private Double latitude;
    private Double longitude;

    // One property can have many photos. Cascade ensures photos are deleted
    // when the property itself is deleted. orderBy keeps the cover photo first.
    @OneToMany(mappedBy = "home", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("displayOrder ASC")
    @JsonIgnoreProperties("home") // prevents infinite loop when serializing to JSON
    private List<HomePhoto> photos = new ArrayList<>();

    // Manual Getters and Setters
    public Long getHomeId() { return homeId; }
    public void setHomeId(Long homeId) { this.homeId = homeId; }

    public User getLandlord() { return landlord; }
    public void setLandlord(User landlord) { this.landlord = landlord; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public BigDecimal getViewingFee() { return viewingFee; }
    public void setViewingFee(BigDecimal viewingFee) { this.viewingFee = viewingFee; }

    public String getSmartLockCode() { return smartLockCode; }
    public void setSmartLockCode(String smartLockCode) { this.smartLockCode = smartLockCode; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public BigDecimal getPricePerMonth() { return pricePerMonth; }
    public void setPricePerMonth(BigDecimal pricePerMonth) { this.pricePerMonth = pricePerMonth; }

    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }

    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }

    public List<HomePhoto> getPhotos() { return photos; }
    public void setPhotos(List<HomePhoto> photos) { this.photos = photos; }
}





