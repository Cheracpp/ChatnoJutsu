# ChatNoJutsu

ChatNoJutsu is a personal learning project for exploring Spring Boot, WebSocket communication, and
full-stack web development.

## Features

### Implemented Features

- **User Management**
    - User registration with unique username and email
    - Retrieve list of users
    - Friend management (add/remove friends)

- **Authentication**
    - JWT-based user authentication
    - csrf protection using the double submit cookie pattern
    - Secure login and logout mechanisms

- **Messaging**
    - Real-time WebSocket communication
    - Message sending and receiving
    - Message history retrieval

### Technologies

- **Backend**: Spring Boot
- **Database**:
    - PostgreSQL (user information)
    - MongoDB (messages and rooms)
- **Authentication**: JWT
- **Real-time Communication**: WebSocket
- **Containerization**: Docker

### Upcoming Features

- [ ] Comprehensive test coverage
- [ ] Enhanced error handling
- [ ] Improved user profile management

## Getting Started

### Prerequisites

- Java JDK 17
- Maven
- Docker & Docker Compose (recommended)
- IDE with Spring Boot support (optional)

### Option 1: Run with Docker (Recommended)

The easiest way to run the application with all dependencies:

```bash
docker-compose up -d
```

Access the application at `http://localhost:8080`

### Option 2: Local Development with Spring Boot

For active development with auto-managed Docker databases:

1. Clone the repository:
   ```bash
   git clone https://github.com/Cheracpp/ChatNoJutsu.git
   cd ChatNoJutsu
   ```

2. Run the application (databases start automatically):
   ```bash
   mvn spring-boot:run
   ```

The `dev` profile is active by default and will automatically start PostgreSQL and MongoDB
containers.

### Option 3: Manual Setup

If you prefer managing databases yourself:

1. Install and start PostgreSQL and MongoDB manually

2. Update `src/main/resources/application-dev.properties`:
   ```properties
   spring.docker.compose.enabled=false
   ```

3. Configure your database connections

4. Run:
   ```bash
   mvn spring-boot:run
   ```

## Docker Commands

### Development

```bash
# Start all services
docker-compose up -d

# View application logs
docker-compose logs -f app

# Stop all services
docker-compose down

# Stop and remove all data
docker-compose down -v
```

### Production Deployment

1. Create `.env` file from template:
   ```bash
   cp .env.example .env
   ```

2. Update environment variables in `.env`:
   ```properties
   DB_PASSWORD=your_secure_password
   MONGO_USER=admin
   MONGO_PASSWORD=your_secure_password
   SPRING_PROFILES_ACTIVE=prod
   ```

3. Deploy:
   ```bash
   docker-compose up -d --build
   ```

## Configuration Profiles

The application supports multiple profiles:

- **dev** (default): Local development with verbose logging and auto schema updates
- **prod**: Production settings with environment-based configuration and optimized performance

Switch profiles:

```bash
# Via command line
mvn spring-boot:run -Dspring-boot.run.profiles=prod

# Via environment variable
export SPRING_PROFILES_ACTIVE=prod
```

## Running Tests

Execute tests with:

```bash
mvn test
```