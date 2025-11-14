# Lets-Play API — CRUD Reference

This README documents every CRUD operation available in this project, example requests (curl), authentication notes, and verification steps in MongoDB Compass. It also includes a concise checklist you can copy & paste to perform admin-only operations (getting all users).

Base URL (local):
- HTTPS: https://localhost:8444

Notes:
- The app uses HTTPS with a self-signed certificate. Use `-k` with curl or disable SSL checking in Thunder Client.
- MongoDB default connection: mongodb://localhost:27017, database `userproductdb`.

----

## Authentication (Create / Login)

### Register (Create user)
- Method: POST
- URL: `/api/auth/register`
- Body (JSON):
```json
{
	"name": "Test User",
	"email": "test@example.com",
	"password": "password123"
}
```

curl example:
```bash
curl -k -X POST "https://localhost:8444/api/auth/register" \
	-H "Content-Type: application/json" \
	-d '{"name":"Test User","email":"test@example.com","password":"password123"}'
```

### Login (Obtain JWT)
- Method: POST
- URL: `/api/auth/login`
- Body (JSON):
```json
{
	"email": "test@example.com",
	"password": "password123"
}
```

curl example:
```bash
curl -k -X POST "https://localhost:8444/api/auth/login" \
	-H "Content-Type: application/json" \
	-d '{"email":"test@example.com","password":"password123"}'
```

Response includes a `token` (JWT). Use it as `Authorization: Bearer <token>` for protected endpoints.

----

## Users (CRUD)

> Base path: `/api/users`. Authorization rules:
- `GET /api/users` — ADMIN only
- `GET /api/users/{id}` — ADMIN or the user themself
- `PUT /api/users/{id}` — ADMIN or the user themself
- `DELETE /api/users/{id}` — ADMIN only

### Get all users (READ)
- Method: GET
- URL: `/api/users`
- Auth: Bearer token (ADMIN role required)

curl:
```bash
curl -k -X GET "https://localhost:8444/api/users" \
	-H "Authorization: Bearer <ADMIN_JWT>"
```

### Get user by id (READ)
- Method: GET
- URL: `/api/users/{id}`
- Auth: ADMIN or same user

### Update user (UPDATE)
- Method: PUT
- URL: `/api/users/{id}`
- Auth: ADMIN or same user
- Body: JSON like registration (name/password)

### Delete user (DELETE)
- Method: DELETE
- URL: `/api/users/{id}`
- Auth: ADMIN

----

## Products (CRUD)

> Base path: `/api/products`. Some endpoints are public; others require authentication.

### Get all products (READ - public)
- Method: GET
- URL: `/api/products`

curl:
```bash
curl -k -X GET "https://localhost:8444/api/products"
```

### Create product (CREATE)
- Method: POST
- URL: `/api/products`
- Auth: Bearer token (any authenticated user)
- Body example:
```json
{
	"name": "Gaming Laptop",
	"description": "High-performance gaming laptop",
	"price": 1299.99,
	"category": "Electronics"
}
```

curl:
```bash
curl -k -X POST "https://localhost:8444/api/products" \
	-H "Authorization: Bearer <JWT>" \
	-H "Content-Type: application/json" \
	-d '{"name":"Gaming Laptop","description":"High-performance","price":1299.99,"category":"Electronics"}'
```

### Get / Update / Delete product by id
- `GET /api/products/{id}` — public
- `PUT /api/products/{id}` — owner or ADMIN
- `DELETE /api/products/{id}` — owner or ADMIN

### Search and user-specific products
- `GET /api/products/search?q={query}` — search
- `GET /api/products/user/{userId}` — products created by a user

----

## MongoDB: verify data

Open MongoDB Compass and connect to `mongodb://localhost:27017`.

Database: `userproductdb`
- `users` — user documents
- `products` — product documents

Shell examples:
```js
use userproductdb
db.users.find().pretty()
db.products.find().pretty()
# Promote a user to admin
db.users.updateOne({ email: "test@example.com" }, { $set: { role: "ADMIN" } })
```

----

## Quick checklist (copy & paste)

Follow these exact steps to get all users (admin-only endpoint):

1. Ensure MongoDB and the app are running
	 - Start MongoDB (Homebrew):
		 ```bash
		 brew services start mongodb-community
		 ```
	 - Start application (project root):
		 ```bash
		 ./mvnw spring-boot:run
		 ```

2. Register a user
	 - POST `/api/auth/register` with JSON:
		 ```json
		 { "name":"Test User","email":"test@example.com","password":"password123" }
		 ```

3. Promote the user to ADMIN (required to call GET /api/users)
	 - Using mongosh:
		 ```js
		 mongosh "mongodb://localhost:27017/userproductdb"
		 db.users.updateOne({ email: "test@example.com" }, { $set: { role: "ADMIN" } })
		 ```

4. Login and obtain JWT
	 - POST `/api/auth/login` with JSON:
		 ```json
		 { "email":"test@example.com","password":"password123" }
		 ```
	 - Copy the `token` from response

5. Call GET /api/users
	 - Header: `Authorization: Bearer <TOKEN>`
	 - Example (curl, skip SSL verification locally):
		 ```bash
		 curl -k -X GET "https://localhost:8444/api/users" -H "Authorization: Bearer <TOKEN>"
		 ```

6. Verify in MongoDB Compass: open `userproductdb` → `users` collection

----

If you want me to perform any of these steps for you, tell me which step (for example: "Promote email test@example.com to ADMIN" or "Run login and GET /api/users") and I'll run it.
