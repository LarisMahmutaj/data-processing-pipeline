# Data Processing Pipeline

Spring Boot application implementing two main features:

1. **Data Processing Pipeline** - A flexible pipeline system for processing JSON data
2. **City Data Aggregator** - An API that aggregates city information from multiple sources

## Quick Start

### Prerequisites

- [Docker](https://docs.docker.com/get-docker/)
- [Docker Compose](https://docs.docker.com/compose/install/)

### Run the Application

```bash
# Clone the repository
git clone <repository-url>
cd data-processing-pipeline

# Start everything
docker compose up

# Wait for: "Started DataProcessingPipelineApplication"
# Application available at: http://localhost:8080
```

That's it! The application will automatically:

- Build the Spring Boot application
- Start PostgreSQL database
- Run database migrations
- Load 5 test users

## Test the APIs

### 1. City Aggregator (Public - No Auth)

```bash
curl http://localhost:8080/api/cities/summary
```

Returns location, description, weather, and video data for cities in `src/main/resources/cities.txt`.

### 2. Authentication

```bash
curl -X POST http://localhost:8080/api/authenticate \
  -H "Content-Type: application/json" \
  -d '{
    "usernameOrEmail": "admin@test.com",
    "password": "password123"
  }'
```

Copy the JWT token from the response.

### 3. Pipeline APIs (Requires Auth)

```bash
# List all pipelines
curl http://localhost:8080/api/pipelines/all \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"

# Get pipeline by ID
curl http://localhost:8080/api/pipelines/1 \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"

# Execute a pipeline
curl -X POST http://localhost:8080/api/pipelines/run/1 \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

## Test Users

All users have password: `password123`

| Email           | Role   | Permissions             |
|-----------------|--------|-------------------------|
| admin@test.com  | Admin  | Full access             |
| editor@test.com | Editor | Create/modify pipelines |
| viewer@test.com | Viewer | Read-only access        |
| owner@test.com  | Owner  | Execute pipelines       |
| basic@test.com  | Basic  | Limited access          |

## Configuration (Optional)

The application works out of the box with default settings. To customize, create a `.env` file:

```bash
# Copy the example file
cp .env.example .env

# Edit the file with your values
JWT_SECRET=your_secure_random_string_here
CITIES_FILE_PATH=/path/to/your/cities.txt  # Optional: use custom cities file
OPENWEATHER_API_KEY=your_key_here  # Get from: https://openweathermap.org/api
YOUTUBE_API_KEY=your_key_here      # Get from: https://console.cloud.google.com/
```

**Cities File:**
- Default: `src/main/resources/cities.txt` (7 sample cities)
- To use your own: Set `CITIES_FILE_PATH` in `.env` to your file path
- To edit the default: Modify `src/main/resources/cities.txt` (one city per line)

**Note:** Without API keys, the City Aggregator still returns location and description data.

## Stopping the Application

```bash
# Stop containers (keeps data)
docker compose down

# Stop and remove all data
docker compose down -v
```

**Components:**

- PostgreSQL 15 with Liquibase migrations
- Spring Boot 4.0.2 with JWT authentication
- 5 node types: Import, Transform, Filter, Sort, Export
- Role-based access control (RBAC)
- External API integration (Nominatim, Wikipedia, OpenWeather, YouTube)

## Key Features

**Feature 1: Data Processing Pipeline**

- JWT authentication
- Create/manage/execute pipelines
- Five node types with various operations
- Role-based permissions (READ, WRITE, EXECUTE)
- Database and file import/export
- HTTP API import

**Feature 2: City Data Aggregator**

- Parallel API calls for performance
- Aggregates data from 4 sources
- Works without API keys (partial data)
- Public endpoint (no auth required)

## Additional Documentation

- [DOCKER.md](DOCKER.md) - Docker usage and troubleshooting

## Technologies

- Spring Boot 4.0.2, Spring Security, Spring Data JPA
- PostgreSQL 15, Liquibase
- JWT authentication
- Docker & Docker Compose

## Troubleshooting

**Port already in use:**

- PostgreSQL (5433): Your local PostgreSQL might be running
- App (8080): Change to `8081:8080` in docker-compose.yml

**Build fails:**

```bash
docker compose build --no-cache
```

**Reset everything:**

```bash
docker compose down -v && docker compose up
```

See [DOCKER.md](DOCKER.md) for more details.
