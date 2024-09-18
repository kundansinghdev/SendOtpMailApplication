package com.SendOtpMailApplication.controller;

import com.SendOtpMailApplication.model.OtpRequest;
import com.SendOtpMailApplication.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api") // Base URL for all endpoints in this controller
public class EmailController {

    @Autowired
    private EmailService emailService; // Injects EmailService to handle email-related logic

    // Home endpoint to provide information about the available APIs
    @GetMapping
    public String home() {
        return "Welcome to the OTP Email Service. Use the API to send and verify OTPs.";
    }

    // Endpoint to send OTP to a provided email
    @PostMapping("/sendotp")
    public String sendOtpEmail(@RequestBody OtpRequest otpRequest) {
        try {
            emailService.sendOtpEmail(otpRequest.getEmail()); // Call service to send OTP
            return "An OTP has been sent to your email. Please check your inbox.";
        } catch (Exception e) {
            return "Failed to send OTP. Please check the email address and try again after 12 hours.";
        }
    }

    // Endpoint to verify the OTP provided by the user
    @PostMapping("/verifyotp")
    public String verifyOtp(@RequestBody OtpRequest otpRequest) {
        try {
            return emailService.verifyOtp(otpRequest.getEmail(), otpRequest.getOtp()); // Call service to verify OTP
        } catch (Exception e) {
            return "An error occurred while verifying the OTP. Please try again.";
        }
    }
}
