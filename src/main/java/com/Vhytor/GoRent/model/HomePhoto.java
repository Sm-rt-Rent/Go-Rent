package com.Vhytor.GoRent.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "Home_photos")
@Data
public class HomePhoto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long homePhotoId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "home_id", nullable = false)
    private Home home;

    @Column(nullable = false)
    private String url;

    @Column(name = "public_id", nullable = false)
    private String publicId;

    // Controls display order in the gallery (0 = cover photo, shown first)
    @Column(nullable = false)
    private Integer displayOrder = 0;

    public HomePhoto() {}




    public void setHomePhotoId(Long homePhotoId) {
        this.homePhotoId = homePhotoId;
    }

    public Long getHomePhotoId() {
        return homePhotoId;
    }

    public Home getHome() { return home; }
    public void setHome(Home home) { this.home = home; }

    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }

    public String getPublicId() { return publicId; }
    public void setPublicId(String publicId) { this.publicId = publicId; }

    public Integer getDisplayOrder() { return displayOrder; }
    public void setDisplayOrder(Integer displayOrder) { this.displayOrder = displayOrder; }
}