# PantryPal – Server

**PantryPal** is a smart pantry management mobile app, designed to help users track their food inventory, manage shopping lists, and scan items using NFC technology.
The PantryPal backend server is built using Java Spring Boot and serves as the core API for managing inventory, shopping lists, user data, NFC scanning, and analytics.

## Features

- RESTful API to manage pantry inventory and shopping lists
- User management with role-based authorization
- Support for Admin, Operator, and End User roles
- Expiration date and inventory stock tracking for trend analysis
- Compatible with React Native frontend via HTTP requests

## Technologies Used

- Java 21
- Spring Boot
- Spring Web
- Spring Data JPA
- MongoDB (based on configuration)
- Maven
- REST API
- CORS configuration for frontend communication

## Installation

1. **Clone the repository**
   ```bash
   git clone https://github.com/your-username/pantrypal-server.git
   cd pantrypal-server
   ```

2. **Configure application properties**

   In `src/main/resources/application.properties`, set up your desired database and server port:

   ```properties
   spring.datasource.url=jdbc:postgresql://localhost:5432/pantry
   spring.datasource.username=your_db_user
   spring.datasource.password=your_db_password

   spring.jpa.hibernate.ddl-auto=update
   server.port=8084
   ```

3. **Build and run**
   ```bash
   mvn spring-boot:run
   ```

## API Base URL

```
http://localhost:8084/ambient-intelligence
```

## Common Endpoints

- `GET /ambient-intelligence/objects` – Retrieve all pantry objects
- `POST /ambient-intelligence/objects` – Add a new object
- `PUT /ambient-intelligence/objects/{systemId}/{objectId}` – Update object details
- `GET /ambient-intelligence/users/login` – Simulated login via external card system
- `GET /ambient-intelligence/admin/...` – Admin-specific operations 

## Security and Access Control

Role-based permissions are enforced at the business logic level. Users are assigned roles such as:

- **Admin** – Full control over users, objects, and commands
- **Operator** – Can view and manage objects
- **End User** – Can execute commands and have limited access to objects based on predefined restrictions

