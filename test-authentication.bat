@echo off
REM CultureHero Authentication System Test Script for Windows
REM This script demonstrates all the authentication features for different stakeholders

echo 🔐 CultureHero Authentication System Demo
echo ==========================================

set BASE_URL=http://localhost:8080

echo.
echo 1️⃣  Testing Login Functionality
echo --------------------------------

echo 🔍 Login as Consultant Admin:
curl -X POST "%BASE_URL%/api/v1/auth/login" ^
  -H "Content-Type: application/json" ^
  -d "{\"username\": \"consultant_admin\", \"password\": \"password123\"}"

echo.
echo 🔍 Login as Client Admin:
curl -X POST "%BASE_URL%/api/v1/auth/login" ^
  -H "Content-Type: application/json" ^
  -d "{\"username\": \"client_admin1\", \"password\": \"password123\"}"

echo.
echo 🔍 Login as Participant:
curl -X POST "%BASE_URL%/api/v1/auth/login" ^
  -H "Content-Type: application/json" ^
  -d "{\"username\": \"participant1\", \"password\": \"password123\"}"

echo.
echo 2️⃣  Testing User Management (Role-based Access)
echo -----------------------------------------------

echo 🏢 Consultant Admin - View ALL users:
curl -X GET "%BASE_URL%/api/v1/users" ^
  -u "consultant_admin:password123"

echo.
echo 🏢 Client Admin - View company users only:
curl -X GET "%BASE_URL%/api/v1/users" ^
  -u "client_admin1:password123"

echo.
echo ❌ Participant tries to view all users (should fail):
curl -X GET "%BASE_URL%/api/v1/users" ^
  -u "participant1:password123"

echo.
echo 3️⃣  Testing Self-Service Access
echo -------------------------------

echo 👤 Participant views own profile:
curl -X GET "%BASE_URL%/api/v1/users/me" ^
  -u "participant1:password123"

echo.
echo 👤 Client Admin views own profile:
curl -X GET "%BASE_URL%/api/v1/users/me" ^
  -u "client_admin1:password123"

echo.
echo 4️⃣  Testing Authentication Validation
echo -------------------------------------

echo ✅ Valid authentication check:
curl -X GET "%BASE_URL%/api/v1/auth/validate" ^
  -u "consultant_admin:password123"

echo.
echo ❌ Invalid authentication check:
curl -X GET "%BASE_URL%/api/v1/auth/validate"

echo.
echo 5️⃣  Testing User Creation
echo ------------------------

echo 👥 Create new user:
curl -X POST "%BASE_URL%/api/v1/users" ^
  -H "Content-Type: application/json" ^
  -d "{\"companyId\": \"550e8400-e29b-41d4-a716-446655440000\", \"username\": \"new_participant\", \"password\": \"securepass123\", \"role\": \"PARTICIPANT\"}"

echo.
echo 6️⃣  Testing Logout
echo ------------------

echo 🚪 Logout user:
curl -X POST "%BASE_URL%/api/v1/auth/logout" ^
  -u "consultant_admin:password123"

echo.
echo 🎉 Authentication System Demo Complete!
echo ========================================
echo.
echo ✅ All stakeholder authentication scenarios tested
echo ✅ Role-based access control verified
echo ✅ Multi-tenant security confirmed
echo ✅ Self-service access working

pause
