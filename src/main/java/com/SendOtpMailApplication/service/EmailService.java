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

/**
 * Service class for managing OTP operations, including sending OTP emails
 * and verifying OTPs provided by users.
 */
@Service
public class EmailService {

    @Autowired
    private JavaMailSender javaMailSender; // Spring's mail sender for sending emails

    // Storage for OTP data, mapped by email addresses
    private Map<String, OtpData> otpStorage = new HashMap<>();

    // Constants for OTP handling
    private static final int MAX_ATTEMPTS = 2; // Maximum allowed failed attempts
    private static final int LOCKOUT_DURATION_HOURS = 24; // Lockout duration after exceeding failed attempts

    /**
     * Sends an OTP email to the specified email address.
     *
     * @param toEmail The recipient's email address
     * @throws Exception if the user is locked out or an error occurs during sending
     */
    public void sendOtpEmail(String toEmail) throws Exception {
        OtpData storedOtpData = otpStorage.get(toEmail); // Check for existing OTP data

        if (storedOtpData != null) {
            // Handle lockout if the user has exceeded allowed attempts
            if (storedOtpData.getFailedAttempts() >= MAX_ATTEMPTS) {
                LocalDateTime lastFailedAttempt = storedOtpData.getLastFailedAttempt();
                long lockoutDuration = Duration.between(lastFailedAttempt, LocalDateTime.now()).toHours();

                if (lockoutDuration < LOCKOUT_DURATION_HOURS) {
                    sendLockoutEmail(toEmail); // Notify user of lockout
                    throw new Exception("You are locked out due to multiple failed attempts. Try again in 24 hours.");
                } else {
                    // Reset failed attempts after the lockout period
                    storedOtpData = new OtpData(generateOtp(), LocalDateTime.now());
                    otpStorage.put(toEmail, storedOtpData); // Save new OTP
                }
            }
        }

        // Generate and send a new OTP email
        String otp = generateOtp(); // Generate a random OTP
        otpStorage.put(toEmail, new OtpData(otp, LocalDateTime.now())); // Store the OTP data

        // Create the email message
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Your OTP Code");
        message.setText("Hi,\n\nYour OTP code is: " + otp + "\n\n"
                        + "Please use this code to verify your email within 1 minute.\n\n"
                        + "Thanks,\nThe OTP Service Team");

        javaMailSender.send(message); // Send the email
    }

    /**
     * Verifies the OTP provided by the user.
     *
     * @param email The email address to which the OTP was sent
     * @param otp   The OTP entered by the user
     * @return A message indicating success or failure
     */
    public String verifyOtp(String email, String otp) {
        OtpData storedOtpData = otpStorage.get(email); // Retrieve OTP data for the email

        if (storedOtpData == null) {
            return "No OTP found. Please request a new one.";
        }

        // Check if the OTP has expired (valid for 1 minute)
        LocalDateTime generatedTime = storedOtpData.getGeneratedTime();
        long timeElapsed = Duration.between(generatedTime, LocalDateTime.now()).getSeconds();

        if (timeElapsed > 60) {
            otpStorage.remove(email); // Remove expired OTP
            return "OTP has expired. Please request a new one.";
        }

        // Check if the user is locked out
        if (storedOtpData.getFailedAttempts() >= MAX_ATTEMPTS) {
            LocalDateTime lastFailedAttempt = storedOtpData.getLastFailedAttempt();
            long lockoutDuration = Duration.between(lastFailedAttempt, LocalDateTime.now()).toHours();

            if (lockoutDuration < LOCKOUT_DURATION_HOURS) {
                sendLockoutEmail(email); // Notify user of lockout
                return "You are locked out due to multiple failed attempts. Try again in 24 hours.";
            } else {
                // Reset failed attempts after lockout period
                storedOtpData = new OtpData(storedOtpData.getOtp(), storedOtpData.getGeneratedTime());
                otpStorage.put(email, storedOtpData);
            }
        }

        // Verify OTP
        if (storedOtpData.getOtp().equals(otp)) {
            otpStorage.remove(email); // OTP verified, remove it
            return "OTP verified successfully!";
        } else {
            storedOtpData.incrementFailedAttempts(); // Increment failed attempts
            otpStorage.put(email, storedOtpData);

            // Lock out user if attempts exceeded
            if (storedOtpData.getFailedAttempts() >= MAX_ATTEMPTS) {
                sendLockoutEmail(email); // Notify user of lockout
            }

            return "Invalid OTP. Please try again.";
        }
    }

    /**
     * Generates a random 4-digit OTP.
     *
     * @return A string representing the generated OTP
     */
    private String generateOtp() {
        int otp = (int) (Math.random() * 9000) + 1000; // Generate a number between 1000 and 9999
        return String.valueOf(otp);
    }

    /**
     * Sends an email notifying the user that their account is locked out.
     *
     * @param toEmail The recipient's email address
     */
    private void sendLockoutEmail(String toEmail) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Account Locked Due to Multiple Failed Attempts");
        message.setText("Hi,\n\nYou have been locked out due to multiple failed OTP attempts. "
                        + "Please try again after 24 hours.\n\n"
                        + "Thanks,\nThe OTP Service Team");

        javaMailSender.send(message); // Send the lockout email
    }
}
