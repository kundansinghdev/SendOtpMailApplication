
# 🔐 OTP Email Service with Lockout Mechanism

A Spring Boot application that provides secure OTP (One-Time Password) verification via email with a lockout mechanism after multiple failed attempts to enhance security.

## 🌟 Features

- 📧 **Send OTP via Email**: Generates and sends a secure OTP to the user's email address.
- ⏳ **OTP Expiry**: OTP is valid for 1 minute, ensuring quick verification and security.
- 🔒 **Lockout Mechanism**: Locks out the user for 24 hours after 3 failed attempts.
- 📬 **Email Notification on Lockout**: Sends an email to the user when they are locked out.
- ⚙️ **Configurable Settings**: Set OTP length and expiry duration through configuration.

## 🛠️ Technologies Used

- **Java 17**: Programming language.
- **Spring Boot**: For application setup and dependency management.
- **Spring Mail (JavaMailSender)**: Used for sending OTP emails.
- **H2 Database**: (Optional) For testing and prototyping, easily switchable to other databases.
