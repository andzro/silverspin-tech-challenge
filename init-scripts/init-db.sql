-- Create separate databases for ordering-system and shipping-system
CREATE DATABASE ordering_db;
CREATE DATABASE shipping_db;
CREATE DATABASE frontend_db;

-- Create users and grant permissions
CREATE USER ordering_user WITH PASSWORD 'ordering_password';
CREATE USER shipping_user WITH PASSWORD 'shipping_password';
CREATE USER frontend_user WITH PASSWORD 'frontend_password';

GRANT ALL PRIVILEGES ON DATABASE ordering_db TO ordering_user;
GRANT ALL PRIVILEGES ON DATABASE shipping_db TO shipping_user;
GRANT ALL PRIVILEGES ON DATABASE frontend_db TO frontend_user;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON TABLES TO frontend_user;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON SEQUENCES TO frontend_user;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON FUNCTIONS TO frontend_user;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON TYPES TO frontend_user;
GRANT USAGE, CREATE ON SCHEMA public TO frontend_user;

