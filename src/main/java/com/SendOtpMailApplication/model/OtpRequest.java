package com.SendOtpMailApplication.model;

public class OtpRequest {

    private String email; // Email address for OTP requests
    private String otp; // OTP for verification

    // Default constructor
    public OtpRequest() {
    }

    // Parameterized constructor
    public OtpRequest(String email, String otp) {
        this.email = email;
        this.otp = otp;
    }

    // Getter for email
    public String getEmail() {
        return email;
    }

    // Setter for email
    public void setEmail(String email) {
        this.email = email;
    }

    // Getter for OTP
    public String getOtp() {
        return otp;
    }

    // Setter for OTP
    public void setOtp(String otp) {
        this.otp = otp;
    }
}
