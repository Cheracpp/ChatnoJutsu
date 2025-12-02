# ChatNoJutsu

ChatNoJutsu is a personal learning project for exploring Spring Boot, WebSocket communication, and
full-stack web development.

## Features

### Implemented Features

- **User Management**
    - User registration with unique username and email
    - Retrieve list of users
- **Authentication**
    - JWT-based user authentication
    - CSRF protection using the double submit cookie pattern
    - Secure login and logout mechanisms
- **Messaging**
    - Real-time WebSocket communication (STOMP protocol)
    - Message sending and receiving
    - Message history retrieval

### Technologies

- **Backend**: Spring Boot
- **Database**:
    - PostgreSQL (User information)
    - MongoDB (Messages and rooms - configured as a Replica Set)
- **Message Broker**: RabbitMQ (with STOMP plugin)
- **Containerization**: Docker & Docker Compose

### Upcoming Features

- [ ] Comprehensive test coverage
- [ ] Enhanced error handling
- [ ] Improved user profile management

## Getting Started

### Prerequisites

- Docker & Docker Compose

## Installation & Running

### 1. Setup Environment

Clone the repository and prepare the configuration:

```bash
git clone https://github.com/Cheracpp/ChatnoJutsu.git
cd ChatNoJutsu
```

### 2. Prepare Security Keys

**Generate MongoDB Keyfile:**
Since MongoDB runs as a replica set, it requires a shared keyfile for internal authentication. Run
this command to generate it with the correct permissions:

```bash
docker run --rm -v "$PWD/secrets-mongo":/app -w /app alpine sh -c "apk add openssl && openssl rand -base64 756 > keyfile && chmod 400 keyfile && chown 999:999 keyfile"
```

Generate a secure 64-byte Base64 encoded secret for the JWT:

```bash
openssl rand -base64 64
```

### 3. Update environment variables:

Create `.env` file in the project root if you haven't already, and Update it:

```properties
JWT_SECRET=<paste-output-from-openssl-here>
DB_PASSWORD=your_secure_password
MONGO_USER=admin
MONGO_PASSWORD=your_secure_password
SPRING_PROFILES_ACTIVE=prod
RABBITMQ_USER=user
RABBITMQ_PASSWORD=your_secure_password
```

### 4. Initialize Infrastructure

Start the databases and message broker first to initialize the MongoDB Replica Set.

```bash
docker-compose up -d mongo0 mongo1 mongo2 postgres rabbitmq
```

**Initialize MongoDB Replica Set:**
Once the containers are running, you must initialize the replica set configuration manually once.

1. Enter the primary MongoDB container:
   ```bash
   docker exec -it chatnojutsu-mongo-primary mongosh "mongodb://admin:mongo123@localhost:27017/"
   ```
   *(Note: Replace `mongo123` with the `MONGO_PASSWORD` from your .env file)*

2. Inside the MongoDB shell, run the following commands:
   ```javascript
   use admin
   ```
   ```javascript
   rs.initiate({
      _id: "rs0",
      members: [
         { _id: 0, host: "mongo0:27017" },
         { _id: 1, host: "mongo1:27017" },
         { _id: 2, host: "mongo2:27017" }
      ]
   })
   ```
   *If successful, the prompt should change to `rs0 [direct: primary] >` or similar.*

3. Exit the shell:
   ```javascript
   exit
   ```

### 5. Run the Application

Now that the database cluster is active, start the main application:

```bash
docker-compose up -d app
```

## Usage & Access

- **Application API**: `http://localhost:8080`
- **RabbitMQ Management Console**: `http://localhost:15672`
    - User: `user` (or value in .env)
    - Password: `password` (or value in .env)