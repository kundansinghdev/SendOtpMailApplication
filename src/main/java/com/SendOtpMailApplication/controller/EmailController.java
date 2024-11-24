package com.SendOtpMailApplication.controller;

import com.SendOtpMailApplication.model.OtpRequest;
import com.SendOtpMailApplication.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller for managing OTP-related email operations.
 * Provides endpoints for sending and verifying OTPs via email.
 */
@RestController
@RequestMapping("/api") // Base URL for all endpoints in this controller
public class EmailController {

    @Autowired
    private EmailService emailService; // Service layer to handle email-related logic

    /**
     * Home endpoint to provide information about the available APIs.
     *
     * @return A welcome message describing the purpose of the API
     */
    @GetMapping
    public String home() {
        return "Welcome to the OTP Email Service. Use the API to send and verify OTPs.";
    }

    /**
     * Endpoint to send an OTP to the user's email address.
     *
     * @param otpRequest A request object containing the user's email address
     * @return A success or error message
     */
    @PostMapping("/sendotp")
    public String sendOtpEmail(@RequestBody OtpRequest otpRequest) {
        try {
            // Call the service to send an OTP to the specified email
            emailService.sendOtpEmail(otpRequest.getEmail());
            return "An OTP has been sent to your email. Please check your inbox.";
        } catch (Exception e) {
            return "Failed to send OTP. Please check the email address and try again after 12 hours.";
        }
    }

    /**
     * Endpoint to verify the OTP provided by the user.
     *
     * @param otpRequest A request object containing the email address and OTP
     * @return A success or error message based on the verification result
     */
    @PostMapping("/verifyotp")
    public String verifyOtp(@RequestBody OtpRequest otpRequest) {
        try {
            // Call the service to verify the provided OTP for the given email
            return emailService.verifyOtp(otpRequest.getEmail(), otpRequest.getOtp());
        } catch (Exception e) {
            return "An error occurred while verifying the OTP. Please try again.";
        }
    }
}
