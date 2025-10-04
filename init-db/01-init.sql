-- Initialize CultureHero database
-- This script runs automatically when PostgreSQL container starts for the first time

-- Create additional schemas if needed
-- CREATE SCHEMA IF NOT EXISTS auth;
-- CREATE SCHEMA IF NOT EXISTS tenant;
-- Grant permissions
GRANT
ALL
PRIVILEGES
ON
DATABASE
culturehero TO postgres;

create schema IF NOT EXISTS culturehero;

GRANT
USAGE
ON
SCHEMA
culturehero TO postgres;

-- Optional: Create extensions if needed for UUID generation
CREATE
EXTENSION IF NOT EXISTS "uuid-ossp";

