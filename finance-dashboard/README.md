# Finance Dashboard — Backend

A Spring Boot backend for a role-based finance dashboard system supporting financial record management, user access control, and summary-level analytics.

## Tech Stack Used

| Layer | Technology |
|-------|------------|
| Language | Java 17 |
| Framework | Spring Boot 3.2 |
| Security | Spring Security + JWT (stateless) |
| Persistence | Spring Data JPA + TiDB Cloud |
| Database | TiDB Cloud (MySQL-compatible) |
| Utilities | Lombok, Bean Validation |

## Project Structure

```
src/main/java/com/finance/dashboard/
├── config/           # Security configuration
├── controller/       # REST API endpoints
├── dto/              # Request and response objects
│   ├── request/
│   └── response/
├── entity/           # JPA entities (no relationship mappings)
├── enums/            # Role, UserStatus, TransactionType
├── exception/        # Custom exceptions + global handler
├── repository/       # JPA repositories with custom queries
├── security/         # JWT provider, filter, UserDetailsService
├── service/          # Business logic layer
└── util/             # ApiResponse wrapper
```

## Setup & Running

### Prerequisites
- Java 17+
- Maven 3.8+
- A TiDB Cloud account (free tier works fine)

### 1. Configure environment variables

Set the following before running:

```
export DB_USERNAME=tidb_username
export DB_PASSWORD=tidb_password
export JWT_SECRET=secret_key_minimum_32_characters_long
```

### 2. Update application.yml

Open `src/main/resources/application.yml` and replace:
- `<tidb-host>` → TiDB Cloud host (e.g. `gateway01.ap-southeast-1.prod.aws.tidbcloud.com`)
- `<db-name>` → Database name (e.g. `test`)

### 3. Run the application

```
./mvnw spring-boot:run
```

The schema (`users` and `transactions` tables) is created automatically on first startup via `schema.sql`.

Server runs at: `http://localhost:8080`

---

## Roles & Permissions

| Sl No | Actions | VIEWER | ANALYST | ADMIN |
|-------|----------------------------------|--------|------------|-------|
| 1 | Register / Login | YES | YES | YES |
| 2 | View transactions | YES | YES | YES |
| 3 | View dashboard summary | YES | YES | YES |
| 4 | Create transactions | NO | YES | YES |
| 5 | Update transactions | NO | ONLY OWN | ALL |
| 6 | Delete transactions | NO | ONLY OWN | ALL |
| 7 | View all users | NO | NO | YES |
| 8 | Create users with specific role | NO | NO | YES |
| 9 | Change user status | NO | NO | YES |

> Self-registration via `/api/auth/register` always assigns the `VIEWER` role.
> Admins can create users with any role via `/api/users`.

---

## API Reference

All protected endpoints require the header:

```
Authorization: Bearer <token>
```

All responses follow this shape:

```json
{
  "success": true,
  "message": "...",
  "data": { ... },
  "timestamp": "2024-01-01T10:00:00"
}
```

---

### Auth

| Method | Endpoint | Auth Required | Description |
|--------|----------------------|---------------|-------------------------------|
| POST | /api/auth/register | No | Register (gets VIEWER role) |
| POST | /api/auth/login | No | Login, returns JWT token |

Request body:

```json
{
  "fullName": "John Doe",
  "email": "john@example.com",
  "password": "secret123"
}
```

---

### Users (ADMIN only)

| Method | Endpoint | Description |
|--------|------------------------|---------------------------|
| GET | /api/users | List all users |
| GET | /api/users/{id} | Get user by ID |
| POST | /api/users | Create user with any role |
| PATCH | /api/users/{id}/status | Activate or deactivate |

Create user body:

```json
{
  "fullName": "Jane Smith",
  "email": "jane@example.com",
  "password": "secret123",
  "role": "ANALYST"
}
```

Update status body:

```json
{
  "status": "INACTIVE"
}
```

---

### Transactions

| Method | Endpoint | Roles | Description |
|--------|---------------------------|---------------------|---------------------------|
| POST | /api/transactions | ADMIN, ANALYST | Create a transaction |
| GET | /api/transactions | ALL | List with optional filters |
| GET | /api/transactions/{id} | ALL | Get by ID |
| PUT | /api/transactions/{id} | ADMIN, ANALYST | Update transaction |
| DELETE | /api/transactions/{id} | ADMIN, ANALYST | Soft delete |

Create / Update body:

```json
{
  "amount": 1500.00,
  "type": "INCOME",
  "category": "Salary",
  "date": "2024-03-15",
  "notes": "March salary"
}
```

Filter query params for `GET /api/transactions`:

| Param | Type | Example |
|----------|--------|----------------------|
| type | enum | INCOME or EXPENSE |
| category | string | Salary |
| from | date | 2024-01-01 |
| to | date | 2024-03-31 |

---

### Dashboard

| Method | Endpoint | Roles | Description |
|--------|------------------------|-------|---------------------|
| GET | /api/dashboard/summary | ALL | Full summary report |

Response includes:
- `totalIncome` — sum of all INCOME transactions
- `totalExpenses` — sum of all EXPENSE transactions
- `netBalance` — income minus expenses
- `categoryTotals` — map of category → total amount
- `monthlyTrends` — last 6 months broken down by type
- `recentActivity` — last 10 transactions

---

## Design Decisions & Assumptions

1. **No JPA relationship mappings** — entities use plain `userId` foreign key fields. Joins are done explicitly via `@Query` when needed. This avoids lazy loading issues and keeps entities simple.

2. **Soft delete** — deleted transactions set `is_deleted = true`. They never appear in any query but remain in the database for audit purposes.

3. **Self-registration is always VIEWER** — only admins can assign ANALYST or ADMIN roles when creating users via `/api/users`.

4. **Analyst ownership rule** — analysts can create, update, and delete their own transactions only. Admins can operate on all.

5. **Stateless JWT auth** — no sessions. Each request is authenticated independently via the JWT filter.

6. **TiDB Cloud / MySQL** — chosen because financial data is relational and SQL aggregations (SUM, GROUP BY) are a natural fit for dashboard queries.

7. **No pagination** — kept out of scope for simplicity. Can be added later with Spring Data's `Pageable`.
