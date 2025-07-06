# PantryPal Client

**PantryPal** is a smart pantry management mobile app built with **React Native** , designed to help users track their food inventory, manage shopping lists, and scan items using NFC technology.

---

## Features

-  View and manage pantry inventory
-  Build shopping lists
-  Track expiration dates of products
-  Visualize pantry trends and statistics
-  NFC integration for scanning products
-  Role-based access for Admins and Operators and Users

---

## Technologies Used

- React Native (Expo)
- JavaScript
- Axios
- React Navigation
- react-native-chart-kit
- react-native-nfc-manager
- Custom backend API (`/ambient-intelligence`)

---

##  Installation

1. **Clone the repository**:
   ```bash
   git clone https://github.com/your-username/pantrypal-client.git
   cd pantrypal-client
   ```

2. **Install Dependencies**:
  ```bash
  npm install
  ```
3. **Start the app**:
  ```bash
  npm run android
  ```
---
# PantryPal Server

PantryPal is a Smart Pantry Management System designed to help users track their kitchen inventory, receive expiration and low-stock alerts, and manage shopping lists. This repository contains the backend API, **built with Spring Boot and MongoDB.**

## Table of Contents

- [Technologies](#technologies)  
- [Prerequisites](#prerequisites)  
- [Installation](#installation)  
- [Configuration](#configuration)  
- [Database Setup](#database-setup)  
- [Running the Server](#running-the-server)  
- [API Endpoints](#api-endpoints)  
- [Testing](#testing)  
- [Docker (optional)](#docker-optional)  
- [Contributing](#contributing)  
- [License](#license)  

## Technologies

- Java 17  
- Spring Boot  
- Spring Data MongoDB  
- Gradle (wrapper included)  
- MongoDB  
- Swagger / OpenAPI  

## API Endpoints

| Method   | Path                         | Description                            |
|----------|------------------------------|----------------------------------------|
| `POST`   | `/api/auth/signup`           | Create a new user account              |
| `POST`   | `/api/auth/login`            | Authenticate user                      |
| `GET`    | `/api/users`                 | List all users (admin only)            |
| `GET`    | `/api/inventory`             | Get current pantry items               |
| `POST`   | `/api/inventory`             | Add a new inventory item               |
| `PUT`    | `/api/inventory/{id}`        | Update quantity or expiration          |
| `DELETE` | `/api/inventory/{id}`        | Remove an item                         |
| `GET`    | `/api/shopping-list`         | Retrieve shopping list                 |
| `POST`   | `/api/shopping-list`         | Add item to shopping list              |
| `DELETE` | `/api/shopping-list/{id}`    | Remove from shopping list              |


## Running the Server

Use the Gradle wrapper to build and run:

```bash
./gradlew clean bootRun
```

The API will be available at  
`http://localhost:${APP_PORT:-8080}/api`

You can view the Swagger UI at:  
`http://localhost:${APP_PORT:-8080}/swagger-ui.html`



