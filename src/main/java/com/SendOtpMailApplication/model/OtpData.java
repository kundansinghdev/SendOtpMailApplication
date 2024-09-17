package com.SendOtpMailApplication.model;

import java.time.LocalDateTime;

public class OtpData {
    private String otp; // The OTP value
    private LocalDateTime generatedTime; // The time when the OTP was generated
    private int failedAttempts; // Number of failed attempts to verify OTP
    private LocalDateTime lastFailedAttempt; // Time of the last failed OTP attempt

    // Constructor to initialize OTP data
    public OtpData(String otp, LocalDateTime generatedTime) {
        this.otp = otp;
        this.generatedTime = generatedTime;
        this.failedAttempts = 0; // Initial failed attempts
        this.lastFailedAttempt = null; // No failed attempts yet
    }

    // Getter for OTP
    public String getOtp() {
        return otp;
    }

    // Getter for OTP generation time
    public LocalDateTime getGeneratedTime() {
        return generatedTime;
    }

    // Getter for number of failed attempts
    public int getFailedAttempts() {
        return failedAttempts;
    }

    // Increment failed attempts count
    public void incrementFailedAttempts() {
        this.failedAttempts++;
        this.lastFailedAttempt = LocalDateTime.now(); // Update last failed attempt time
    }

    // Getter for last failed attempt time
    public LocalDateTime getLastFailedAttempt() {
        return lastFailedAttempt;
    }
}
