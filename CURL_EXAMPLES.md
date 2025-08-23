# CultureHero Authentication API - Sample cURL Commands

## Base URL
BASE_URL="http://localhost:8080"

## 1. Create a new user (No authentication required)
curl -X POST "$BASE_URL/api/v1/users" \
  -H "Content-Type: application/json" \
  -d '{
    "companyId": "550e8400-e29b-41d4-a716-446655440000",
    "username": "testuser",
    "password": "password123",
    "role": "PARTICIPANT"
  }'

## 2. Create a consultant admin
curl -X POST "$BASE_URL/api/v1/users" \
  -H "Content-Type: application/json" \
  -d '{
    "companyId": "550e8400-e29b-41d4-a716-446655440001",
    "username": "consultant_admin",
    "password": "securepass123",
    "role": "CONSULTANT_ADMIN"
  }'

## 3. Create a client admin
curl -X POST "$BASE_URL/api/v1/users" \
  -H "Content-Type: application/json" \
  -d '{
    "companyId": "550e8400-e29b-41d4-a716-446655440000",
    "username": "client_admin",
    "password": "securepass456",
    "role": "CLIENT_ADMIN"
  }'

## 4. Get current user info (Requires HTTP Basic Authentication)
curl -X GET "$BASE_URL/api/v1/users/me" \
  -u "testuser:password123"

## 5. Get current user info with consultant admin
curl -X GET "$BASE_URL/api/v1/users/me" \
  -u "consultant_admin:securepass123"

## 6. Test authentication failure
curl -X GET "$BASE_URL/api/v1/users/me" \
  -u "testuser:wrongpassword"

## 7. Test validation failure (short username and password)
curl -X POST "$BASE_URL/api/v1/users" \
  -H "Content-Type: application/json" \
  -d '{
    "companyId": "550e8400-e29b-41d4-a716-446655440000",
    "username": "ab",
    "password": "123",
    "role": "PARTICIPANT"
  }'

## 8. Test unauthorized access (no credentials)
curl -X GET "$BASE_URL/api/v1/users/me"

## 9. Test duplicate username (should fail)
curl -X POST "$BASE_URL/api/v1/users" \
  -H "Content-Type: application/json" \
  -d '{
    "companyId": "550e8400-e29b-41d4-a716-446655440000",
    "username": "testuser",
    "password": "password123",
    "role": "PARTICIPANT"
  }'

## Expected HTTP Status Codes:
# - 201: User created successfully
# - 200: User info retrieved successfully
# - 400: Validation error (short username/password)
# - 401: Authentication failed (wrong credentials or no credentials)
# - 409: Conflict (duplicate username)

## Response Examples:

### Successful user creation (201):
{
  "id": "123e4567-e89b-12d3-a456-426614174000",
  "companyId": "550e8400-e29b-41d4-a716-446655440000",
  "username": "testuser",
  "role": "PARTICIPANT",
  "createdAt": "2025-08-23T08:00:00.000Z",
  "updatedAt": "2025-08-23T08:00:00.000Z"
}

### Validation error (400):
{
  "type": "about:blank",
  "title": "Validation Failed",
  "status": 400,
  "detail": "Request validation failed",
  "errors": {
    "username": "size must be between 3 and 50",
    "password": "size must be at least 8"
  }
}

### Authentication error (401):
{
  "timestamp": "2025-08-23T08:00:00.000+00:00",
  "status": 401,
  "error": "Unauthorized",
  "path": "/api/v1/users/me"
}
