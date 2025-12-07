# MediaMarketplace – Backend

![Java](https://img.shields.io/badge/Java-24-informational?style=flat&logo=java&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.5-informational?style=flat&logo=spring&logoColor=white)
![MySQL](https://img.shields.io/badge/MySQL-8-informational?style=flat&logo=mysql&logoColor=white)

MediaMarketplace is a full-stack movie-rental platform where users can browse movies, rent or buy them, leave ratings, and watch short previews.
This repository contains the **backend API** built with Spring Boot and MySQL.

## Overview

The backend provides all the endpoints to manage users, movies, ratings, rentals, and previews.
It’s designed as a RESTful API that the React frontend consumes.

## Features
- User authentication & authorization
- Movie management (CRUD)
- Rental and purchase workflow
- Movie ratings management
- MySQL database integration
- REST API built with Spring Boot
- Fully functional backend, ready for frontend integration

## Tech Stack
- Java 24
- Spring Boot
- Spring Data / JPA
- MySQL
- RESTful API design

## Running the Backend
```bash
git clone https://github.com/Darth4Vader/MediaMarketplaceWeb
cd MediaMarketplaceWeb
./mvnw clean install
./mvnw spring-boot:run
```
> Update application.properties with your MySQL credentials before running.

The backend runs by default on: http://localhost:8080

## Project Structure
- `controller/` – API endpoints
- `service/` – Business logic
- `repository/` – Database interactions
- `entities/` – Entity classes
- Organized for maintainability and scalability

## What I Focused On

One of the main challenges was **designing a clean, maintainable API structure** that could scale and be easily consumed by the frontend.

## Future Improvements
- Payment gateway integration
- Admin dashboard
- API documentation (Swagger/OpenAPI)
- Automated tests (JUnit)

## About the Project

Built alone, end-to-end backend.
Demonstrates skills in designing APIs, database schemas, and a maintainable backend architecture.
