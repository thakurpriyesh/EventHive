<div align="center">

# 🎟️ EventHive

### Discover events. Choose your seat. Make memories.

A full-stack event management and ticket booking platform built with **Spring Boot, React, and MySQL**.

<br>

[![Java](https://img.shields.io/badge/Java-21-orange?style=for-the-badge&logo=openjdk)](https://www.java.com/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.1.1-6DB33F?style=for-the-badge&logo=springboot)](https://spring.io/projects/spring-boot)
[![React](https://img.shields.io/badge/React-Vite-61DAFB?style=for-the-badge&logo=react)](https://react.dev/)
[![MySQL](https://img.shields.io/badge/MySQL-9.6-4479A1?style=for-the-badge&logo=mysql)](https://www.mysql.com/)
[![Maven](https://img.shields.io/badge/Maven-C71A36?style=for-the-badge&logo=apachemaven)](https://maven.apache.org/)

</div>

---

## ✨ About

**EventHive** is a full-stack event management and ticket booking platform.

Users can discover events, view event details, choose tickets and seats, and manage their bookings.

Organizers can create and manage events, configure ticket types and seating, and view booking information.

The application focuses on real-world backend challenges including:

- Authentication and authorization
- Database relationships
- Transactions
- Ticket management
- Seat allocation
- Concurrent booking protection

---

## 🚀 Features

<details>
<summary><strong>🔐 Authentication & Security</strong></summary>

<br>

- User registration and login
- Password hashing with BCrypt
- Google OAuth login
- JWT authentication
- Role-based authorization
- `USER` and `ORGANIZER` roles

</details>

<details>
<summary><strong>🎪 Event Management</strong></summary>

<br>

- Create events
- Edit and delete events
- Event categories
- Event listing
- Search and filtering
- Event details
- Event banners/images

</details>

<details>
<summary><strong>🎫 Tickets & Seats</strong></summary>

<br>

- Multiple ticket types
- Ticket pricing
- Ticket quantity management
- Event seating
- Available/booked seat display
- Seat selection
- Seat allocation during booking
- Duplicate seat booking prevention

</details>

<details>
<summary><strong>📋 Booking</strong></summary>

<br>

- Ticket and seat selection
- Booking creation
- Automatic total calculation
- Booking confirmation
- View bookings
- Booking details
- Booking cancellation where applicable

</details>

<details>
<summary><strong>📊 Organizer Dashboard</strong></summary>

<br>

- View managed events
- Ticket sales
- Booking statistics
- Basic revenue information
- Attendee information
- Booked seat information

</details>

---

## 🛠️ Tech Stack

| Category | Technology |
|---|---|
| Language | Java 21 |
| Backend | Spring Boot |
| API | Spring Web |
| ORM | Spring Data JPA / Hibernate |
| Security | Spring Security |
| Authentication | JWT / OAuth 2.0 |
| Password Hashing | BCrypt |
| Database | MySQL |
| Frontend | React + Vite |
| Build Tool | Maven |
| Version Control | Git / GitHub |

---

## 🏗️ Architecture

```text
┌─────────────────────────┐
│      React + Vite       │
│        Frontend         │
└────────────┬────────────┘
             │
             │ REST API
             ▼
┌─────────────────────────┐
│       Spring Boot       │
│         Backend         │
│                         │
│   Controllers           │
│   Services              │
│   Repositories          │
│   Security              │
└────────────┬────────────┘
             │
             │ JPA / Hibernate
             ▼
┌─────────────────────────┐
│          MySQL          │
│        Database         │
└─────────────────────────┘
```

---

## 🗄️ Core Domain Model

```text
User
 │
 ├── Booking
 │      │
 │      └── BookingSeat
 │               │
 │               └── Seat
 │
 └── Organizer
         │
         └── Event
                │
                ├── Category
                ├── TicketType
                └── Seat
```

---

## 🔄 Booking Flow

```text
Browse Events
      │
      ▼
Select Event
      │
      ▼
Choose Ticket / Seat
      │
      ▼
Check Availability
      │
      ▼
Allocate Seat
      │
      ▼
Create Booking
      │
      ▼
Calculate Total
      │
      ▼
Confirm Booking
```

A key part of EventHive is ensuring that the same seat cannot be successfully booked by multiple users, including when multiple booking requests happen concurrently.

---

## 📁 Project Structure

```text
eventhive/
│
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/app/eventhive/
│   │   │       ├── config/
│   │   │       ├── controller/
│   │   │       ├── dto/
│   │   │       ├── entity/
│   │   │       ├── repository/
│   │   │       ├── security/
│   │   │       └── service/
│   │   │
│   │   └── resources/
│   │       └── application.properties
│   │
│   └── test/
│
├── .gitignore
├── mvnw
├── mvnw.cmd
├── pom.xml
└── README.md
```

---

## ⚙️ Getting Started

### Prerequisites

Make sure you have installed:

- Java 21+
- MySQL
- Node.js
- Git

---

### Clone the repository

```bash
git clone <repository-url>
cd eventhive
```

---

### Create the database

```sql
CREATE DATABASE eventhive;
```

Create a dedicated MySQL user and grant it access to the database.

Example:

```sql
CREATE USER 'eventhive_user'@'localhost' IDENTIFIED BY 'your-password';

GRANT ALL PRIVILEGES ON eventhive.* TO 'eventhive_user'@'localhost';

FLUSH PRIVILEGES;
```

---

### Configure environment variables

EventHive keeps sensitive credentials outside the repository.

Set your database password:

```bash
export DB_PASSWORD="your-password"
```

The backend uses:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/eventhive
spring.datasource.username=eventhive_user
spring.datasource.password=${DB_PASSWORD}
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
```

> [!IMPORTANT]
> Never commit database passwords, API keys, JWT secrets, or OAuth credentials to GitHub.

---

## ▶️ Running the Backend

Using Maven Wrapper:

```bash
./mvnw spring-boot:run
```

Or with Maven installed globally:

```bash
mvn spring-boot:run
```

---

## 🧪 Running Tests

Using Maven Wrapper:

```bash
./mvnw test
```

Or:

```bash
mvn test
```

---

## 🔐 Security

Sensitive values are supplied through environment variables instead of being hardcoded.

For example:

```properties
spring.datasource.password=${DB_PASSWORD}
```

This keeps secrets outside the Git repository while allowing the application to access them locally or through deployment environment configuration.

---

<div align="center">

## 🎟️ EventHive

**Discover. Book. Experience.**

⭐ Star the repository if you find the project interesting.

</div>