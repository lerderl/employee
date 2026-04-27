# Employee Management API

A Spring Boot REST API for managing employees with support for CRUD operations, Excel import/export, and PDF report generation.

---

## 🚀 Features

* Create, update, and retrieve employees
* Pagination and filtering
* Soft delete (deactivate) and hard delete
* Excel import (`.xlsx`) with validation and error reporting
* Excel export (`.xlsx`)
* PDF report generation
* Global exception handling
* Bean validation (`jakarta.validation`)

---

## 🛠️ Tech Stack

* Java 21+
* Spring Boot
* Spring Data JPA
* Hibernate Validator
* Apache POI (Excel)
* OpenPDF (PDF generation)
* Lombok
* H2 / MySQL (configurable)

---

## 📦 Project Setup

### 1. Clone the repository

```bash
git clone https://github.com/lerderl/employee
cd employee
```

---

### 2. Configure database

#### Option A: H2 (default)

No setup required. Access console:

```
http://localhost:8080/h2-console
```

#### Option B: MySQL

Update `application.properties`:

```properties
spring.datasource.url=jdbc:h2:mem:employeesdb;DB_CLOSE_DELAY=-1
spring.datasource.driver-class-name=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
spring.jpa.hibernate.ddl-auto=create-drop
spring.jpa.show-sql=true
spring.h2.console.enabled=true
spring.h2.console.path=/h2-console
```

---

### 3. Build and run

```bash
mvn clean install
mvn spring-boot:run
```

App runs at:

```
http://localhost:8080
```

---

## 📘 API Documentation

If Swagger is enabled:

```
http://localhost:8080/swagger-ui/index.html
```

---

# 📡 API Endpoints

Base URL:

```
/api/v1/employees
```

---

## 🔹 Create Employee

**POST** `/api/v1/employees`

```json
{
  "firstName": "John",
  "lastName": "Doe",
  "email": "john@example.com",
  "department": "IT",
  "salary": 50000,
  "dateOfJoining": "2023-01-15",
  "active": true
}
```

**Response:** `201 Created`

---

## 🔹 Get All Employees (Paginated)

**GET** `/api/v1/employees?page=0&size=10&sort=firstName,asc&department=IT&active=true`

**Response:** `200 OK`

---

## 🔹 Get Employee by ID

**GET** `/api/v1/employees/{id}`

**Response:**

* `200 OK`
* `404 Not Found`

---

## 🔹 Update Employee (Full)

**PUT** `/api/v1/employees/{id}`

**Response:** `200 OK`

---

## 🔹 Partial Update

**PATCH** `/api/v1/employees/{id}`

```json
{
  "salary": 60000,
  "department": "Finance",
  "active": true
}
```

---

## 🔹 Soft Delete

**DELETE** `/api/v1/employees/{id}`

* Sets `active = false`

**Response:** `204 No Content`

---

## 🔹 Hard Delete (Purge)

**DELETE** `/api/v1/employees/{id}/hard`

* Only allowed if `active = false`

**Response:** `204 No Content`

---

## 🔹 Salary Range Filter

**GET** `/api/v1/employees/salary-range?min=10000&max=60000`

**Response:** `200 OK`

---

# 📥 Excel Import

## Endpoint

**POST** `/api/v1/employees/import`

* Content-Type: `multipart/form-data`
* Field name: `file`

---

## Expected Excel Format (`.xlsx`)

| firstName | lastName | email | department | salary | dateOfJoining | active |
| --------- | -------- | ----- | ---------- | ------ | ------------- | ------ |

---

## Response

```json
{
  "successCount": 5,
  "failureCount": 2,
  "errors": [
    "Row 2: firstName - must not be blank"
  ]
}
```

---

# 📤 Excel Export

## Endpoint

**GET** `/api/v1/employees/export/excel?department=IT&active=true`

### Response Headers

```
Content-Type: application/vnd.openxmlformats-officedocument.spreadsheetml.sheet
Content-Disposition: attachment; filename="employees_<timestamp>.xlsx"
```

---

# 📄 PDF Report

## Endpoint

**GET** `/api/v1/employees/export/pdf`

### Response Headers

```
Content-Type: application/pdf
Content-Disposition: attachment; filename="employee_report_<timestamp>.pdf"
```

---

## PDF Includes

* Company name and report title
* Generated timestamp
* Total employee count
* Styled table (alternating rows)
* Salary formatted as currency
* Inactive employees struck through
* Footer: Page X of Y

---

# ⚠️ Business Rules

* Email must be unique
* Salary rules:

    * Intern ≥ 15,000
    * Others ≥ 30,000
* Soft delete uses `active = false`
* Hard delete only allowed for inactive employees

---

# 🧪 Running Tests

```bash
mvn test
```

---

# 📁 Test Resources

Place and find sample Excel files in:

```
src/test/resources/
```

---

# ❗ Error Handling

Standard response format:

```json
{
  "status": 400,
  "message": "Validation failed",
  "errors": {
    "firstName": "must not be blank"
  }
}
```

---

# 👨‍💻 Author

Name: Joseph Olukunle

Email: [josepholukunle1107@gmail.com](mailto:josepholukunle1107@gmail.com)

---