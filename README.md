# ChatNoJutsu

ChatNoJutsu is a chat server built with Spring Boot, utilizing WebSockets for real-time communication. As a learning project, it aims to illustrate best practices in building and testing REST APIs and real-time services using Spring Boot.

## Features

### Current Features

- **User Management**
    - Register a new user (`POST /users`)
    - Retrieve a list of users (`GET /users`)

- **Authentication**
    - Authenticate a user and return a token (`POST /auth/login`)

### Upcoming Features

- [ ] WebSocket communication for real-time chat functionality.
- [ ] Group chat creation and management.
- [ ] Message persistence and retrieval (chat history).
- [ ] User profile management (update user details, profile picture, etc.).

## Getting Started

These instructions will help you get a copy of ChatNoJutsu running on your local machine for development and testing purposes.

### Prerequisites
mvn
- Java JDK 21
- Maven
- H2 Database (for development and testing purposes, can be run in-memory)


### Installing

1. Clone the repository:
   ```bash
   git clone https://github.com/Cheracpp/ChatNoJutsu.git
2. Navigate to the project directory:
    ```bash
   cd ChatNoJutsu
3. Build the project using Maven:
    ```bash
   mvn clean install
4. Run the application:
    ```bash
   java -jar target/ChatnoJutsu-0.0.1-SNAPSHOT.jar
The server will start at http://localhost:8080.

### Running the Tests

Execute the unit tests with:

```bash
    mvn test
