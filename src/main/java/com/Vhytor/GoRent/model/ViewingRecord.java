package com.Vhytor.GoRent.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "viewing_records")
@Data
public class ViewingRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "viewing_record_id")
    private Long viewingRecordId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "home_id",nullable = false)
    private Home home;

    private String accessCode; // The unique code generated for this user
    private boolean paid;
    private boolean used = false; // New field to track
    private LocalDateTime expiryTime;

    private String transactionReference;
    @Column(name = "status", columnDefinition = "VARCHAR(20) DEFAULT 'PENDING'")
    private String status; // PENDING, SUCCESSFUL

    public ViewingRecord() {}

    public Long getViewingRecordId() { return viewingRecordId; }
    public void setViewingRecordId(Long viewingRecordId) { this.viewingRecordId = viewingRecordId; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public Home getHome() { return home; }
    public void setHome(Home home) { this.home = home; }

    public String getAccessCode() { return accessCode; }
    public void setAccessCode(String accessCode) { this.accessCode = accessCode; }

    public boolean isPaid() { return paid; }
    public void setPaid(boolean paid) { this.paid = paid; }

    public boolean isUsed() { return used; }
    public void setUsed(boolean used) { this.used = used; }

    public LocalDateTime getExpiryTime() { return expiryTime; }
    public void setExpiryTime(LocalDateTime expiryTime) { this.expiryTime = expiryTime; }

    public String getTransactionReference() { return transactionReference; }
    public void setTransactionReference(String transactionReference) { this.transactionReference = transactionReference; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

}


