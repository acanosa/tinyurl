# URL Shortener API

A scalable URL shortening service built with **Java 21**, **Spring Boot**, **PostgreSQL**, **Redis**, and **Docker Compose**.

The application allows users to create short URLs that redirect to their original destinations, using Redis as a high-performance cache layer and PostgreSQL as the persistent storage.

## Features

* Create shortened URLs
* Redirect short URLs to original URLs
* Unique short code generation using Base62 encoding
* Redis caching for fast URL resolution
* PostgreSQL persistence
* Automatic database initialization through JPA/Hibernate
* Containerized development environment with Docker Compose
* RESTful API design

## Architecture

The application follows a layered architecture:

```
Client
  |
  v
Spring Boot REST API
  |
  +----------------+
  |                |
  v                v
Redis Cache     PostgreSQL
(Cache layer)   (Persistence)
```

### Request flow

### Creating a short URL

1. Client sends the original URL.
2. Application generates a unique short code.
3. URL mapping is stored in PostgreSQL.
4. The generated short URL is returned.

### Redirecting

1. Client requests a short URL.
2. Application checks Redis first.
3. If found, the original URL is returned immediately.
4. If not found:

   * The URL is retrieved from PostgreSQL.
   * The result is stored in Redis.
   * The redirect is completed.

This approach reduces database load and improves response times for frequently accessed URLs.

---

## Tech Stack

### Backend

* Java 21
* Spring Boot
* Spring Web
* Spring Data JPA
* Hibernate
* Maven

### Database & Cache

* PostgreSQL
* Redis

### Infrastructure

* Docker
* Docker Compose

---

## Running the Application

### Requirements

Make sure you have installed:

* Java 21
* Maven
* Docker Desktop

---

### 1. Clone the repository

```bash
git clone <repository-url>
cd url-shortener
```

---

### 2. Start infrastructure services

Run PostgreSQL and Redis:

```bash
docker compose up -d
```

This will start:

* PostgreSQL database
* Redis cache

---

### 3. Run the application

Using Maven:

```bash
mvn spring-boot:run
```

The API will be available at:

```
http://localhost:8080
```




## 🧮 Short Code Generation

The application generates short identifiers using **Base62 encoding**:

```
Characters:
[a-z][A-Z][0-9]

Example:

Database ID:
123456

Base62:
w7E
```

This provides compact and URL-friendly identifiers.
