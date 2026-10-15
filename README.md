<div align="center">

# 🎟️ EventHive

### Discover events. Choose your seat. Make memories.

A backend REST API for event management and ticket booking, built with **Spring Boot and MySQL**.

<br>

[![Java](https://img.shields.io/badge/Java-21-orange?style=for-the-badge&logo=openjdk)](https://www.java.com/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.1.1-6DB33F?style=for-the-badge&logo=springboot)](https://spring.io/projects/spring-boot)
[![MySQL](https://img.shields.io/badge/MySQL-9.6-4479A1?style=for-the-badge&logo=mysql)](https://www.mysql.com/)
[![Maven](https://img.shields.io/badge/Maven-C71A36?style=for-the-badge&logo=apachemaven)](https://maven.apache.org/)

</div>

---

## ✨ About

**EventHive** is a REST API backend for event management and ticket booking.

Users can discover events, view details, choose tickets and seats, and manage their bookings.

Organizers can create and manage events, configure ticket types and seating, and view booking statistics.

The backend focuses on real-world challenges including:

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
- JWT authentication (JJWT 0.12.x)
- Role-based authorization
- `USER` and `ORGANIZER` roles
- `@PreAuthorize` method-level security

</details>

<details>
<summary><strong>🎪 Event Management</strong></summary>

<br>

- Create, update, and delete events (organizer only)
- Event categories
- Paginated event listing
- Search and filtering by title, location, category, status
- Event status lifecycle: `UPCOMING`, `ONGOING`, `CANCELLED`, `COMPLETED`

</details>

<details>
<summary><strong>🎫 Tickets & Seats</strong></summary>

<br>

- Multiple ticket types per event (e.g., VIP, General)
- Ticket pricing and quantity management
- Seat creation and assignment to ticket types
- Available/booked seat tracking
- Duplicate seat booking prevention via unique constraint + pessimistic locking

</details>

<details>
<summary><strong>📋 Booking</strong></summary>

<br>

- Ticket and seat selection
- Transactional booking creation
- Automatic total calculation
- View bookings
- Booking cancellation (restores seat availability and ticket quantity)
- Organizer view of all bookings for their events

</details>

<details>
<summary><strong>📊 Organizer Dashboard</strong></summary>

<br>

- Total events count
- Total confirmed bookings
- Total revenue

</details>

---

## 🛠️ Tech Stack

| Category | Technology |
|---|---|
| Language | Java 21 |
| Backend | Spring Boot 4.1.1 |
| API | Spring Web MVC |
| ORM | Spring Data JPA / Hibernate |
| Security | Spring Security 7.x |
| Authentication | JWT (JJWT 0.12.6) |
| Password Hashing | BCrypt |
| Database | MySQL |
| Build Tool | Maven |
| Version Control | Git / GitHub |

---

## 🏗️ Architecture

```text
Client (REST)
      │
      │ HTTP / JSON
      ▼
┌─────────────────────────┐
│       Spring Boot       │
│         Backend         │
│                         │
│   Controllers           │
│   Services              │
│   Repositories          │
│   Security (JWT)        │
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

## 🗄️ Domain Model

```text
User
 │
 ├── Booking
 │      │
 │      └── BookingSeat
 │               │
 │               └── Seat
 │
 └── Event (as Organizer)
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
Choose Ticket Type / Seats
      │
      ▼
Check Availability (pessimistic lock)
      │
      ▼
Allocate Seats (mark isBooked = true)
      │
      ▼
Decrease availableQuantity
      │
      ▼
Create Booking + BookingSeats
      │
      ▼
Calculate Total (price × quantity)
      │
      ▼
Return BookingResponse
```

A key part of EventHive is ensuring that the same seat cannot be successfully booked by multiple users concurrently. This is handled via `@Lock(LockModeType.PESSIMISTIC_WRITE)` on the seat fetch + a `UNIQUE` constraint on `booking_seats.seat_id`.

---

## 📁 Project Structure

```text
eventhive/
│
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/app/eventhive/
│   │   │       ├── controller/
│   │   │       │   ├── AuthController.java
│   │   │       │   ├── BookingController.java
│   │   │       │   ├── CategoryController.java
│   │   │       │   ├── EventController.java
│   │   │       │   └── OrganizerController.java
│   │   │       ├── dto/
│   │   │       │   ├── AuthResponse.java
│   │   │       │   ├── BookingRequest.java
│   │   │       │   ├── BookingResponse.java
│   │   │       │   ├── CategoryResponse.java
│   │   │       │   ├── EventRequest.java
│   │   │       │   ├── EventResponse.java
│   │   │       │   ├── GoogleAuthRequest.java
│   │   │       │   ├── GoogleUserInfo.java
│   │   │       │   ├── LoginRequest.java
│   │   │       │   ├── OrganizerStatsResponse.java
│   │   │       │   ├── RegisterRequest.java
│   │   │       │   ├── SeatRequest.java
│   │   │       │   └── TicketTypeRequest.java
│   │   │       ├── entity/
│   │   │       │   ├── Booking.java
│   │   │       │   ├── BookingSeat.java
│   │   │       │   ├── BookingStatus.java
│   │   │       │   ├── Category.java
│   │   │       │   ├── Event.java
│   │   │       │   ├── EventStatus.java
│   │   │       │   ├── Role.java
│   │   │       │   ├── Seat.java
│   │   │       │   ├── TicketType.java
│   │   │       │   └── User.java
│   │   │       ├── exception/
│   │   │       │   ├── BadRequestException.java
│   │   │       │   ├── GlobalExceptionHandler.java
│   │   │       │   └── ResourceNotFoundException.java
│   │   │       ├── repository/
│   │   │       │   ├── BookingRepository.java
│   │   │       │   ├── BookingSeatRepository.java
│   │   │       │   ├── CategoryRepository.java
│   │   │       │   ├── EventRepository.java
│   │   │       │   ├── SeatRepository.java
│   │   │       │   ├── TicketTypeRepository.java
│   │   │       │   └── UserRepository.java
│   │   │       ├── security/
│   │   │       │   ├── JwtAuthenticationFilter.java
│   │   │       │   ├── JwtUtil.java
│   │   │       │   ├── SecurityConfig.java
│   │   │       │   └── SecurityUtils.java
│   │   │       └── service/
│   │   │           ├── AuthService.java
│   │   │           ├── BookingService.java
│   │   │           ├── CategoryService.java
│   │   │           ├── EventService.java
│   │   │           ├── GoogleOAuthService.java
│   │   │           └── OrganizerService.java
│   │   │
│   │   └── resources/
│   │       └── application.properties
│   │
│   └── test/
│       ├── java/com/app/eventhive/
│       │   ├── controller/
│       │   │   ├── AuthControllerTest.java
│       │   │   ├── BookingControllerTest.java
│       │   │   ├── CategoryControllerTest.java
│       │   │   ├── EventControllerTest.java
│       │   │   └── OrganizerControllerTest.java
│       │   ├── exception/
│       │   │   └── GlobalExceptionHandlerTest.java
│       │   ├── security/
│       │   │   └── JwtUtilTest.java
│       │   ├── service/
│       │   │   ├── AuthServiceTest.java
│       │   │   ├── BookingServiceTest.java
│       │   │   ├── CategoryServiceTest.java
│       │   │   ├── EventServiceTest.java
│       │   │   ├── GoogleOAuthServiceTest.java
│       │   │   └── OrganizerServiceTest.java
│       │   └── EventhiveApplicationTests.java
│       └── resources/
│
├── .gitignore
├── mvnw
├── mvnw.cmd
├── pom.xml
└── README.md
```

---

## 🌐 API Endpoints

### Auth — `/api/auth`

| Method | Endpoint | Access | Description |
|---|---|---|---|
| POST | `/api/auth/register` | Public | Register a new user |
| POST | `/api/auth/login` | Public | Login with email/password and receive JWT |
| POST | `/api/auth/google` | Public | Login / auto-register with Google OAuth 2.0 credential |

### Categories — `/api/categories`

| Method | Endpoint | Access | Description |
|---|---|---|---|
| GET | `/api/categories` | Public | List all categories |
| POST | `/api/categories` | ORGANIZER | Create a category |

### Events — `/api/events`

| Method | Endpoint | Access | Description |
|---|---|---|---|
| GET | `/api/events` | Public | List/search events (paginated) |
| GET | `/api/events/{id}` | Public | Get event details |
| POST | `/api/events` | ORGANIZER | Create an event |
| PUT | `/api/events/{id}` | ORGANIZER | Update an event |
| DELETE | `/api/events/{id}` | ORGANIZER | Delete an event |
| GET | `/api/events/my` | ORGANIZER | Get my events |
| POST | `/api/events/{id}/ticket-types` | ORGANIZER | Add ticket types to event |
| POST | `/api/events/{id}/seats` | ORGANIZER | Add seats to event |
| GET | `/api/events/{id}/seats` | Public | Get all seats for event |
| GET | `/api/events/{id}/seats/available` | Public | Get available seats |

### Bookings — `/api/bookings`

| Method | Endpoint | Access | Description |
|---|---|---|---|
| POST | `/api/bookings` | Authenticated | Create a booking |
| GET | `/api/bookings` | Authenticated | Get my bookings |
| GET | `/api/bookings/{id}` | Authenticated | Get booking details |
| PUT | `/api/bookings/{id}/cancel` | Authenticated | Cancel a booking |
| GET | `/api/bookings/organizer` | ORGANIZER | Get all bookings for my events |

### Organizer — `/api/organizer`

| Method | Endpoint | Access | Description |
|---|---|---|---|
| GET | `/api/organizer/stats` | ORGANIZER | Get dashboard stats |

---

## ⚙️ Getting Started

### Prerequisites

- Java 21+
- MySQL
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

Create a dedicated MySQL user:

```sql
CREATE USER 'eventhive_user'@'localhost' IDENTIFIED BY 'your-password';
GRANT ALL PRIVILEGES ON eventhive.* TO 'eventhive_user'@'localhost';
FLUSH PRIVILEGES;
```

---

### Configure environment variables

Set your credentials as environment variables before running:

```bash
export DB_PASSWORD="your-db-password"
export JWT_SECRET="your-base64-encoded-secret-at-least-32-chars"
# Optional: set Google Client ID to validate token audience
export GOOGLE_CLIENT_ID="your-google-client-id.apps.googleusercontent.com"
```

The `application.properties` reads:

```properties
spring.datasource.password=${DB_PASSWORD}
jwt.secret=${JWT_SECRET}
google.client-id=${GOOGLE_CLIENT_ID:}
```

> [!IMPORTANT]
> Never commit database passwords, JWT secrets, or API keys to Git. Always supply them via environment variables.

---

## 🔑 Google OAuth 2.0 Login

The backend supports authentication with Google OAuth 2.0 tokens (e.g. from Google Sign-In button / Google Identity Services).

Send the Google ID token or credential to `POST /api/auth/google`:

```bash
curl -X POST http://localhost:8080/api/auth/google \
  -H "Content-Type: application/json" \
  -d '{"idToken": "<GOOGLE_ID_TOKEN>"}'
```

*(Note: The request payload also accepts `{"credential": "<TOKEN>"}` or `{"token": "<TOKEN>"}`)*

**Flow:**
1. Backend verifies the token with Google's token verification service (`https://oauth2.googleapis.com/tokeninfo`).
2. Checks that the Google account email is verified and matches `GOOGLE_CLIENT_ID` (if set).
3. If the user already exists in the database, they are authenticated.
4. If the user does not exist, an account is automatically created with `ROLE_USER`.
5. Returns a standard `AuthResponse` containing the backend JWT token. All subsequent API calls use `Authorization: Bearer <token>`.

---

## ▶️ Running the Backend

```bash
./mvnw spring-boot:run
```

The API will be available at `http://localhost:8080`.

Tables are auto-created/updated by Hibernate (`ddl-auto=update`).

---

## 🧪 Running Tests

```bash
./mvnw test
```

---

## 🔐 Security Notes

- Passwords are hashed with BCrypt before storage.
- JWT tokens expire after 24 hours (configurable via `jwt.expiration`).
- Sensitive configuration is supplied through environment variables only.
- Seat booking is protected against race conditions using pessimistic DB-level locking (`PESSIMISTIC_WRITE`) and a unique constraint on `booking_seats.seat_id`.

---

<div align="center">

## 🎟️ EventHive

**Discover. Book. Experience.**

⭐ Star the repository if you find the project interesting.

</div>