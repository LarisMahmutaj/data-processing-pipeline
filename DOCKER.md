# Docker Guide

Quick reference for Docker commands and common operations.

## Quick Start

```bash
# Start the application
docker compose up

# Start in background
docker compose up -d

# Stop the application
docker compose down

# Stop and remove all data
docker compose down -v
```

## Services

### PostgreSQL Database

- **Container**: `dpp-postgres`
- **Image**: `postgres:15-alpine`
- **Host Port**: 5433 (to avoid conflict with local PostgreSQL)
- **Container Port**: 5432
- **Database**: `data_processing_pipeline`
- **Credentials**: postgres/postgres

### Spring Boot Application

- **Container**: `dpp-app`
- **Port**: 8080
- **Builds from**: Local Dockerfile
- **Depends on**: PostgreSQL health check

## Environment Variables

Configure in `.env` file:

```bash
JWT_SECRET=your_secure_random_string_here
CITIES_FILE_PATH=/path/to/your/cities.txt
OPENWEATHER_API_KEY=your_key_here
YOUTUBE_API_KEY=your_key_here
```

**Note:** The application has sensible defaults and works without a `.env` file. Create one only if you need to customize values.

### Customizing Cities

**Option 1: Edit the default file**
```bash
# Edit src/main/resources/cities.txt
# Restart the app: docker compose restart app
```

**Option 2: Use your own file**
```bash
# Create your cities file
echo -e "Tokyo\nParis\nSydney" > my-cities.txt

# Add to .env
echo "CITIES_FILE_PATH=$(pwd)/my-cities.txt" >> .env

# Restart
docker compose up
```