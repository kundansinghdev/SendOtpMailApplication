package com.SendOtpMailApplication.service;

import com.SendOtpMailApplication.model.OtpData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service // Marks this class as a Spring service component
public class EmailService {

    @Autowired
    private JavaMailSender javaMailSender; // Injects JavaMailSender to send emails

    // Store OTP data mapped to the email addresses
    private Map<String, OtpData> otpStorage = new HashMap<>();

    // Constants for OTP handling
    private static final int MAX_ATTEMPTS = 2; // Maximum failed attempts allowed
    private static final int LOCKOUT_DURATION_HOURS = 24; // Lockout duration after exceeding failed attempts

    // Method to send an OTP to the user's email
    public void sendOtpEmail(String toEmail) throws Exception {
        OtpData storedOtpData = otpStorage.get(toEmail); // Check if there is existing OTP data

        if (storedOtpData != null) {
            // If user has failed too many times, check lockout status
            if (storedOtpData.getFailedAttempts() >= MAX_ATTEMPTS) {
                LocalDateTime lastFailedAttempt = storedOtpData.getLastFailedAttempt();
                long lockoutDuration = Duration.between(lastFailedAttempt, LocalDateTime.now()).toHours();

                if (lockoutDuration < LOCKOUT_DURATION_HOURS) {
                    // User is still locked out, send an email to notify them of the lockout
                    sendLockoutEmail(toEmail); // Call to send the lockout email
                    throw new Exception("You have been locked out due to multiple failed attempts. Please try again after 24 hours.");
                } else {
                    // Reset failed attempts after lockout period
                    storedOtpData = new OtpData(generateOtp(), LocalDateTime.now());
                    otpStorage.put(toEmail, storedOtpData); // Save the new OTP
                }
            }
        }

        // Generate and send a new OTP email
        String otp = generateOtp(); // Generate a 4-digit OTP
        otpStorage.put(toEmail, new OtpData(otp, LocalDateTime.now())); // Store OTP data

        // Create email content
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail); // Set recipient email address
        message.setSubject("Your OTP Code"); // Set email subject
        message.setText("Hi,\n\nYour OTP code is: " + otp + "\n\n"
                        + "Use this code to verify your email within 1 minute.\n\n"
                        + "Thanks,\nThe OTP Service Team"); // Set email body

        javaMailSender.send(message); // Send the email
    }

    // Method to verify the OTP entered by the user
    public String verifyOtp(String email, String otp) {
        OtpData storedOtpData = otpStorage.get(email); // Retrieve OTP data for the email

        if (storedOtpData == null) {
            return "No OTP found. Please request a new one."; // If no OTP is found, prompt user to request a new one
        }

        // Check if the OTP is expired (valid for only 1 minute)
        LocalDateTime generatedTime = storedOtpData.getGeneratedTime();
        long timeElapsed = Duration.between(generatedTime, LocalDateTime.now()).getSeconds();

        if (timeElapsed > 60) {
            otpStorage.remove(email); // OTP expired, remove it from storage
            return "OTP has expired. Please request a new one.";
        }

        // Check if the user is locked out
        if (storedOtpData.getFailedAttempts() >= MAX_ATTEMPTS) {
            LocalDateTime lastFailedAttempt = storedOtpData.getLastFailedAttempt();
            long lockoutDuration = Duration.between(lastFailedAttempt, LocalDateTime.now()).toHours();

            if (lockoutDuration < LOCKOUT_DURATION_HOURS) {
                // User is locked out, send email to notify them of the lockout
                sendLockoutEmail(email); // Call to send the lockout email
                return "You have been locked out due to multiple failed attempts. Please try again after 24 hours.";
            } else {
                // Reset failed attempts after the lockout period
                storedOtpData = new OtpData(storedOtpData.getOtp(), storedOtpData.getGeneratedTime());
                otpStorage.put(email, storedOtpData); // Update OTP data
            }
        }

        // If OTP is correct, reset the data and return success
        if (storedOtpData.getOtp().equals(otp)) {
            otpStorage.remove(email); // Remove OTP on successful verification
            return "OTP verified successfully!";
        } else {
            // If OTP is incorrect, increment failed attempts
            storedOtpData.incrementFailedAttempts();
            otpStorage.put(email, storedOtpData); // Update OTP data with new failed attempt count

            // Check if the user has reached the maximum number of attempts and lock them out
            if (storedOtpData.getFailedAttempts() >= MAX_ATTEMPTS) {
                sendLockoutEmail(email); // Send email notifying them that they are locked out
            }

            return "Invalid OTP. Please try again.";
        }
    }

    // Method to generate a random 4-digit OTP
    private String generateOtp() {
        int otp = (int) (Math.random() * 9000) + 1000; // Generate a random number between 1000 and 9999
        return String.valueOf(otp); // Return OTP as string
    }

    // Method to send lockout notification email
    private void sendLockoutEmail(String toEmail) {
        // Create email content
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail); // Set recipient email address
        message.setSubject("Account Locked Due to Multiple Failed Attempts"); // Set email subject
        message.setText("Hi,\n\nYou have been locked out due to multiple failed OTP attempts. "
                        + "Please try again after 24 hours.\n\n"
                        + "Thanks,\nThe OTP Service Team"); // Set email body

        javaMailSender.send(message); // Send the lockout email
    }
}
