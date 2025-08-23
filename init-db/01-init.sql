-- Initialize CultureHero database
-- This script runs automatically when PostgreSQL container starts for the first time

-- Create additional schemas if needed
-- CREATE SCHEMA IF NOT EXISTS auth;
-- CREATE SCHEMA IF NOT EXISTS tenant;

-- Grant permissions
GRANT ALL PRIVILEGES ON DATABASE culturehero TO postgres;

-- Create extensions for UUID generation and other useful functions
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
CREATE EXTENSION IF NOT EXISTS "pg_trgm";   -- For text similarity searches
CREATE EXTENSION IF NOT EXISTS "unaccent";  -- For accent-insensitive searches

-- Create indexes for better performance (will be created by Flyway migrations)
-- These are examples for future reference

-- Company reference data (for demonstration)
-- Note: In production, companies would be managed through the application
-- This is just for testing purposes to show the company IDs being used

-- ACME Corp: 550e8400-e29b-41d4-a716-446655440000
-- Tech Startup: 6ba7b810-9dad-11d1-80b4-00c04fd430c8  
-- Global Enterprises: 6ba7b811-9dad-11d1-80b4-00c04fd430c8

COMMENT ON DATABASE culturehero IS 'CultureHero application database with multi-tenant architecture';

-- Log initialization
\echo 'CultureHero database initialized successfully! 🚀'
