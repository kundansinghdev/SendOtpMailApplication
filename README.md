
# OTP Email Service with Lockout Mechanism

This Spring Boot application provides a secure OTP (One-Time Password) verification via email, implementing a lockout mechanism after multiple failed attempts to enhance security.

## Features

- **Send OTP via Email**: Generate and send a secure OTP to the user's email address.
- **OTP Expiry**: The OTP is valid for 1 minute, ensuring quick verification and security.
- **Lockout Mechanism**: After 3 failed attempts, the user is locked out for 24 hours.
- **Email Notification on Lockout**: Sends an email notification to the user when they are locked out.
- **Configurable OTP Length and Expiry**: Set OTP length and expiry duration through configuration.
  
## Technologies Used

- **Java 17**: Core programming language.
- **Spring Boot**: Framework to simplify application setup and dependency management.
- **Spring Mail (JavaMailSender)**: For sending OTP emails.
- **H2 Database**: (Optional) For quick prototyping and testing. Easily switchable to other databases.

## Getting Started

### Prerequisites

- **Java 17** installed on your system.
- **Maven** for dependency management.
- Email configuration for `JavaMailSender` (SMTP settings for the email service provider).

### Installation

1. **Clone the Repository**

   ```bash
   git clone https://github.com/yourusername/otp-email-service.git
   cd otp-email-service
   ```

2. **Configure Email Service**

   Update `application.properties` with your SMTP configuration:

   ```properties
   spring.mail.host=smtp.example.com
   spring.mail.port=587
   spring.mail.username=your-email@example.com
   spring.mail.password=your-email-password
   spring.mail.properties.mail.smtp.auth=true
   spring.mail.properties.mail.smtp.starttls.enable=true
   ```

3. **Build the Application**

   ```bash
   mvn clean install
   ```

4. **Run the Application**

   ```bash
   mvn spring-boot:run
   ```

### Configuration

- **OTP Expiration Time**: Set in `application.properties`:

  ```properties
  otp.expiration-time=1  # OTP validity in minutes
  ```

- **Lockout Settings**: Also configurable in `application.properties`:

  ```properties
  otp.max-attempts=3      # Maximum failed attempts
  otp.lockout-duration=24 # Lockout duration in hours
  ```

## Usage

1. **Request OTP**

   Use the `/api/request-otp` endpoint to initiate OTP generation and email delivery.

   **Example Request:**

   ```http
   POST /api/request-otp
   {
     "email": "user@example.com"
   }
   ```

2. **Verify OTP**

   Verify the OTP within 1 minute using the `/api/verify-otp` endpoint. After 3 failed attempts, the user will be locked out and notified by email.

   **Example Request:**

   ```http
   POST /api/verify-otp
   {
     "email": "user@example.com",
     "otp": "123456"
   }
   ```

3. **Lockout Notification**

   After 3 failed attempts, the system sends a lockout email, notifying the user that their account is locked for 24 hours.
