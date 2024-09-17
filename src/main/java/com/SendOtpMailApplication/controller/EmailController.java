package com.SendOtpMailApplication.controller;

import com.SendOtpMailApplication.model.OtpRequest;
import com.SendOtpMailApplication.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController // Marks this class as a REST controller
@RequestMapping("/api") // Base URL for this controller
public class EmailController {

    @Autowired
    private EmailService emailService; // Injects the EmailService bean

    @GetMapping("/") // Handles GET requests to "/api/"
    public String home() {
        return "Welcome to the OTP Email Service. Use the available API to send and verify OTP.";
    }

    @PostMapping("/sendotp") // Handles POST requests to "/api/sendotp"
    public String sendOtpEmail(@RequestBody OtpRequest otpRequest) {
        try {
            emailService.sendOtpEmail(otpRequest.getEmail()); // Sends OTP to the provided email
            return "An OTP has been sent to your email address. Please check your inbox.";
        } catch (Exception e) {
            return "Failed to send OTP. Please check the email address and Try again after 12 hours.";
        }
    }

    @PostMapping("/verifyotp") // Handles POST requests to "/api/verifyotp"
    public String verifyOtp(@RequestBody OtpRequest otpRequest) {
        try {
            return emailService.verifyOtp(otpRequest.getEmail(), otpRequest.getOtp()); // Verifies the OTP
        } catch (Exception e) {
            return "An error occurred while verifying the OTP. Please try again.";
        }
    }
}
