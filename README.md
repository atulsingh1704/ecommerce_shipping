# 📦 E-Commerce Shipping Charge Estimator

<div align="center">

![Java](https://img.shields.io/badge/Java-21-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.0.2-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white)
![Maven](https://img.shields.io/badge/Maven-3.9+-C71A36?style=for-the-badge&logo=apache-maven&logoColor=white)
![H2](https://img.shields.io/badge/H2-In--Memory-003B57?style=for-the-badge&logo=databricks&logoColor=white)
![Swagger](https://img.shields.io/badge/Swagger-OpenAPI%203-85EA2D?style=for-the-badge&logo=swagger&logoColor=black)
![Lombok](https://img.shields.io/badge/Lombok-1.18-red?style=for-the-badge&logo=java&logoColor=white)

**A production-grade B2B shipping charge estimation microservice for Kirana (grocery) store marketplace logistics.**

[🚀 Getting Started](#-getting-started) · [📡 API Reference](#-api-endpoints) · [🏗️ Architecture](#%EF%B8%8F-architecture) · [🧪 Testing](#-testing)

---

</div>

## 🌟 Overview

This microservice calculates **real-time shipping charges** for a B2B e-commerce platform that connects **sellers** (suppliers) with **Kirana stores** (small grocery retailers) across India. It handles the complete logistics chain:

```
Seller → Nearest Warehouse → Customer (Kirana Store)
```

The system intelligently determines the **nearest warehouse**, selects the optimal **transport mode** based on distance, and applies **delivery speed surcharges** — all through clean, well-documented REST APIs.

---

## 🛠️ Tech Stack & Libraries

<table>
<tr>
<td align="center" width="150">

### ☕ Core
</td>
<td>

| Technology | Version | Purpose |
|:-----------|:--------|:--------|
| **Java** | 21 (LTS) | Language runtime with modern features |
| **Spring Boot** | 4.0.2 | Application framework & auto-configuration |
| **Spring Web MVC** | 7.x | REST controller layer |
| **Spring Data JPA** | 4.x | ORM & repository abstraction |
| **Hibernate** | 7.x | JPA implementation & SQL generation |

</td>
</tr>
<tr>
<td align="center">

### 🗄️ Database
</td>
<td>

| Technology | Purpose |
|:-----------|:--------|
| **H2 Database** | In-memory relational DB (zero setup) |
| **H2 Console** | Built-in web UI at `/h2-console` |

</td>
</tr>
<tr>
<td align="center">

### ⚡ Performance
</td>
<td>

| Technology | Purpose |
|:-----------|:--------|
| **Spring Cache** | Caching abstraction layer |
| **Caffeine** | High-performance in-memory cache (10 min TTL, 500 entries max) |

</td>
</tr>
<tr>
<td align="center">

### 🔧 Dev Tools
</td>
<td>

| Technology | Purpose |
|:-----------|:--------|
| **Lombok** | Eliminates boilerplate (getters, setters, builders) |
| **Jakarta Validation** | Bean validation with `@Valid`, `@NotNull`, `@Positive` |
| **Springdoc OpenAPI** | Auto-generated Swagger UI & API docs |

</td>
</tr>
<tr>
<td align="center">

### 🧪 Testing
</td>
<td>

| Technology | Purpose |
|:-----------|:--------|
| **JUnit 5** | Unit & integration testing framework |
| **Mockito** | Mocking framework for isolated tests |
| **Spring WebMvcTest** | Slice testing for REST controllers |
| **MockMvc** | HTTP request simulation |

</td>
</tr>
</table>

---

## 🏗️ Architecture

The project follows a **layered architecture** with clear separation of concerns:

```
📁 com.jumbotail.ecommerce_shipping
│
├── 🎮 controller/              ← REST API endpoints
│   ├── ShippingController       (Shipping charge APIs)
│   └── WarehouseController      (Nearest warehouse API)
│
├── 📋 dto/                     ← Data Transfer Objects
│   ├── request/
│   │   └── ShippingCalculateRequest
│   └── response/
│       ├── ShippingChargeResponse
│       ├── ShippingCalculateResponse
│       ├── NearestWarehouseResponse
│       └── LocationDto
│
├── 🏛️ entity/                  ← JPA Entities (DB models)
│   ├── Warehouse
│   ├── Seller
│   ├── Product
│   ├── Customer
│   └── Location (Embeddable)
│
├── 🔢 enums/                   ← Type-safe enumerations
│   ├── DeliverySpeed            (STANDARD, EXPRESS)
│   └── TransportMode            (MINI_VAN, TRUCK, AEROPLANE)
│
├── 🗃️ repository/              ← Spring Data JPA Repositories
│   ├── WarehouseRepository
│   ├── SellerRepository
│   ├── ProductRepository
│   └── CustomerRepository
│
├── ⚙️ service/                 ← Business Logic
│   ├── ShippingService          (Interface)
│   ├── WarehouseService         (Interface)
│   ├── DistanceCalculatorService
│   └── impl/
│       ├── ShippingServiceImpl
│       └── WarehouseServiceImpl
│
├── 🎯 strategy/                ← Strategy Design Pattern
│   ├── ShippingCostStrategy     (Interface)
│   ├── StandardShippingStrategy
│   ├── ExpressShippingStrategy
│   └── ShippingStrategyFactory
│
├── 🛡️ exception/               ← Error Handling
│   ├── GlobalExceptionHandler   (@RestControllerAdvice)
│   ├── ResourceNotFoundException
│   ├── InvalidParameterException
│   ├── NoWarehouseFoundException
│   └── ErrorResponse
│
├── 🧮 util/                    ← Utility Classes
│   └── HaversineUtil            (Geo-distance calculation)
│
└── ⚙️ config/                  ← Configuration
    ├── CacheConfig              (Caffeine cache setup)
    └── DataInitializer          (Seed data on startup)
```

### 🎯 Design Patterns Used

| Pattern | Where | Why |
|:--------|:------|:----|
| **Strategy** | `ShippingCostStrategy` | Dynamically select Standard vs Express pricing logic |
| **Factory** | `ShippingStrategyFactory` | Resolve strategy beans by `DeliverySpeed` |
| **Repository** | Spring Data JPA | Abstract away database operations |
| **DTO** | Request/Response objects | Decouple API layer from entity layer |
| **Builder** | Lombok `@Builder` | Clean, readable object construction |
| **Global Exception Handler** | `@RestControllerAdvice` | Centralized, consistent error responses |

---

## 💰 Shipping Charge Formula

The shipping cost is calculated using a transparent, multi-factor formula:

```
Base Charge  = Distance (km) × Chargeable Weight (kg) × Transport Rate (₹/km/kg)
Total Charge = Base Charge + Flat Fee (₹10) + [Express Surcharge if applicable]
```

### 🚚 Transport Modes

| Mode | Distance Range | Rate (₹/km/kg) | Use Case |
|:-----|:---------------|:----------------|:---------|
| 🛻 **MINI_VAN** | 0 – 100 km | ₹3.00 | Local / intra-city |
| 🚛 **TRUCK** | 100 – 500 km | ₹2.00 | Inter-city |
| ✈️ **AEROPLANE** | 500+ km | ₹1.00 | Long-haul / cross-country |

### 📦 Weight Calculation

```
Volumetric Weight = (Length × Width × Height) / 5000  (industry standard)
Chargeable Weight = MAX(Actual Weight, Volumetric Weight)
```

### 🏎️ Delivery Speed Pricing

| Speed | Formula |
|:------|:--------|
| **Standard** | ₹10 flat fee + base charge |
| **Express** | ₹10 flat fee + ₹1.20/kg surcharge + base charge |

---

## 📡 API Endpoints

### Base URL: `http://localhost:8080`

<details>
<summary><b>GET</b> <code>/api/v1/shipping-charge</code> — Get Shipping Charge</summary>

Calculate shipping charge from a specific warehouse to a customer.

**Query Parameters:**

| Parameter | Type | Required | Description |
|:----------|:-----|:---------|:------------|
| `warehouseId` | Long | ✅ | Source warehouse ID |
| `customerId` | Long | ✅ | Destination customer ID |
| `deliverySpeed` | String | ✅ | `standard` or `express` |

**Example Request:**
```http
GET /api/v1/shipping-charge?warehouseId=1&customerId=1&deliverySpeed=standard
```

**Example Response:**
```json
{
    "shippingCharge": 160.00,
    "distanceKm": 50.0,
    "transportMode": "MINI_VAN",
    "deliverySpeed": "standard",
    "warehouseId": 1,
    "customerId": 1
}
```
</details>

<details>
<summary><b>POST</b> <code>/api/v1/shipping-charge/calculate</code> — Full Shipping Calculation</summary>

End-to-end calculation: finds nearest warehouse → computes shipping charge using actual product weight.

**Request Body:**
```json
{
    "sellerId": 1,
    "customerId": 1,
    "productId": 1,
    "deliverySpeed": "express"
}
```

**Example Response:**
```json
{
    "shippingCharge": 803.59,
    "nearestWarehouse": {
        "warehouseId": 1,
        "warehouseCode": "BLR_Warehouse",
        "warehouseName": "Bangalore Fulfillment Center",
        "warehouseLocation": {
            "lat": 12.99999,
            "lng": 37.923273
        },
        "distanceFromSellerKm": 4293.9
    },
    "distanceKm": 1585.97,
    "transportMode": "AEROPLANE",
    "deliverySpeed": "express",
    "chargeableWeightKg": 0.5,
    "productName": "Maggie Noodles 500g Packet"
}
```
</details>

<details>
<summary><b>GET</b> <code>/api/v1/warehouse/nearest</code> — Find Nearest Warehouse</summary>

Find the nearest operational warehouse for a seller's product drop-off.

**Query Parameters:**

| Parameter | Type | Required | Description |
|:----------|:-----|:---------|:------------|
| `sellerId` | Long | ✅ | Seller ID |
| `productId` | Long | ✅ | Product ID (must belong to seller) |

**Example Request:**
```http
GET /api/v1/warehouse/nearest?sellerId=1&productId=1
```

**Example Response:**
```json
{
    "warehouseId": 1,
    "warehouseCode": "BLR_Warehouse",
    "warehouseName": "Bangalore Fulfillment Center",
    "warehouseLocation": {
        "lat": 12.99999,
        "lng": 37.923273
    },
    "distanceFromSellerKm": 4293.9
}
```
</details>

### 📚 Swagger UI

Interactive API documentation is available at:
```
http://localhost:8080/swagger-ui.html
```

---

## 🚀 Getting Started

### Prerequisites

- ☕ **Java 21** or later ([Download](https://adoptium.net/))
- No database installation needed — H2 runs in-memory!

### Clone & Run

```bash
# 1. Clone the repository
git clone https://github.com/atulsingh1704/ecommerce_shipping.git
cd ecommerce_shipping

# 2. Build the project
./mvnw clean package

# 3. Run the application
java -jar target/ecommerce-shipping-1.0.0.jar
```

The server starts at **`http://localhost:8080`** 🎉

### Quick Test

```bash
# Get shipping charge (warehouse → customer)
curl "http://localhost:8080/api/v1/shipping-charge?warehouseId=1&customerId=1&deliverySpeed=standard"

# Full calculation (seller → nearest warehouse → customer)
curl -X POST http://localhost:8080/api/v1/shipping-charge/calculate \
  -H "Content-Type: application/json" \
  -d '{"sellerId":1,"customerId":1,"productId":1,"deliverySpeed":"express"}'

# Find nearest warehouse
curl "http://localhost:8080/api/v1/warehouse/nearest?sellerId=1&productId=1"
```

### Access H2 Console

Navigate to `http://localhost:8080/h2-console` and connect with:
- **JDBC URL:** `jdbc:h2:mem:ecommercedb`
- **Username:** `sa`
- **Password:** *(leave empty)*

---

## 📊 Sample Seed Data

The application auto-seeds sample data on startup:

| Entity | Records | Examples |
|:-------|:--------|:--------|
| 🏭 Warehouses | 2 | BLR_Warehouse (Bangalore), MUMB_Warehouse (Mumbai) |
| 🏪 Sellers | 3 | Nestle India, Rice Seller Co., Sugar Seller Pvt Ltd |
| 📦 Products | 3 | Maggie 500g, Basmati Rice 10Kg, Sugar 25Kg |
| 🛒 Customers | 2 | Shree Kirana Store (Mysore), Andheri Mini Mart (Mumbai) |

---

## 🧪 Testing

Run the full test suite:

```bash
./mvnw test
```

| Test Class | Tests | Scope |
|:-----------|:------|:------|
| `ShippingControllerTest` | 4 | REST API contract tests (MockMvc) |
| `ShippingServiceTest` | — | Service-layer unit tests |
| `WarehouseServiceTest` | — | Nearest warehouse logic tests |

---

## 🛡️ Error Handling

All errors return a consistent JSON structure:

```json
{
    "status": 400,
    "error": "VALIDATION_FAILED",
    "message": "Request validation failed. See fieldErrors for details.",
    "path": "/api/v1/shipping-charge/calculate",
    "timestamp": "2026-03-03T00:20:23.296",
    "fieldErrors": {
        "sellerId": "sellerId is required"
    }
}
```

| HTTP Code | Error Type | Cause |
|:----------|:-----------|:------|
| `400` | `VALIDATION_FAILED` | Missing/invalid request body fields |
| `400` | `BAD_REQUEST` | Invalid business parameters |
| `400` | `INVALID_VALUE` | Unsupported enum value (e.g., delivery speed) |
| `404` | `NOT_FOUND` | Warehouse, Seller, Product, or Customer not found |
| `404` | `NO_WAREHOUSE_FOUND` | No operational warehouses in system |
| `500` | `INTERNAL_SERVER_ERROR` | Unexpected server error |

---

## 📁 Project Structure

```
ecommerce-shipping/
├── src/
│   ├── main/
│   │   ├── java/com/jumbotail/ecommerce_shipping/
│   │   │   ├── EcommerceShippingApplication.java
│   │   │   ├── config/
│   │   │   ├── controller/
│   │   │   ├── dto/
│   │   │   ├── entity/
│   │   │   ├── enums/
│   │   │   ├── exception/
│   │   │   ├── repository/
│   │   │   ├── service/
│   │   │   ├── strategy/
│   │   │   └── util/
│   │   └── resources/
│   │       └── application.properties
│   └── test/
│       └── java/com/jumbotail/ecommerce_shipping/
├── pom.xml
├── mvnw / mvnw.cmd
└── README.md
```

---

## 📜 License

This project is developed as part of an internship assignment.

---

<div align="center">

**Built with ❤️ using Spring Boot & Java 21**

</div>
