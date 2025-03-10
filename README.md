
# Silverspin Tech Challenge

This is my project for the silverspin-tech-challenge. Take note that this was done in a span of 2 days and was not able to focus much due to a jam packed schedule of part time, full time and some personal events that happened when this was requested.

# Order & Shipping Management System

## Overview
This project consists of **three** main services:

1. **Ordering System** (Spring Boot + Kafka + PostgreSQL)
2. **Shipping System** (Spring Boot + Kafka + PostgreSQL)
3. **Frontend System** (Laravel + MySQL)

These services work together to allow users to **place orders**, **process shipments**, and **track deliveries** through an integrated system powered by **Kafka for event-driven communication**.

---

## Architecture

### **1. Ordering System**
- **Tech Stack**: Spring Boot, Kafka, PostgreSQL, Liquibase
- **Role**: Handles order creation, updates, and deletion.
- **Main Features**:
  - Create, update, delete orders.
  - Publishes order events to Kafka for processing.

### **2. Shipping System**
- **Tech Stack**: Spring Boot, Kafka, PostgreSQL, Liquibase
- **Role**: Listens to order events and processes shipments.
- **Main Features**:
  - Listens for orders from Kafka.
  - Creates shipments and updates their statuses.
  - Tracks shipment events.

### **3. Frontend System**
- **Tech Stack**: Laravel, MySQL, GuzzleHTTP
- **Role**: Provides a web interface to interact with the Ordering and Shipping systems.
- **Main Features**:
  - Calls backend APIs using **GuzzleHTTP**.
  - Simulates order creation and shipment tracking.
  - Provides a basic dashboard.

---

## **Project Setup**

### **1. Environment Requirements**
Ensure you have the following installed:
- **Docker & Docker Compose**
- **JDK 21**
- **Gradle**
- **PHP 8.1 & Composer**
- **MySQL & PostgreSQL**

---

### **2. Running the Project**
You can clone the whole project using [Google IDX](https://idx.google.com) for a **seamless setup** on your end.

Once the workspace is set up, start by running:

```sh
docker-compose -f docker-compose-db.yml up -d
docker-compose -f docker-compose-kafka.yml up -d
```

After waiting for the docker containers to be ready, run each application:

#### For the Ordering System & Shipping System
```sh
./gradlew bootRun
```

#### For the Frontend System
```sh
php artisan migrate  # Initial migration of Laravel database
php artisan serve
```



### API Documentation
Both Ordering System and Shipping System have OpenAPI documentation.

- Ordering System: http://localhost:8080/swagger-ui/index.html
- Shipping System: http://localhost:8081/swagger-ui/index.html



## Current Features & TODO

#### ✅ Implemented
- Ordering System CRUD (Create, Read, Update, Delete)
- Kafka integration for order events
- Shipping System event consumer & processing
- Laravel frontend integration with GuzzleHTTP
- Database schema setup with Liquibase
- Docker Compose automation

#### 🛠 Some stuff I wanted to implement, but couldn't find time
- Improve frontend UI for better order/shipping tracking.
- Implement authentication & user roles.
- Enhance order validation & business rules.
- Make the orders realistic by adding more details
- Add unit tests
- Add health checks to endpoints and provide proper handling when APIs are currently down
