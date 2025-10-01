# ChatNoJutsu

ChatNoJutsu is a personal learning project for exploring Spring Boot, WebSocket communication, and full-stack web development.

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

### Upcoming Features

- [ ] Comprehensive test coverage
- [ ] Enhanced error handling
- [ ] Improved user profile management

## Getting Started

### Prerequisites

- Java JDK 17
- Maven
- PostgreSQL database
- MongoDB
- IDE with Spring Boot support (recommended)

### Installation

1. Clone the repository:
   ```bash
   git clone https://github.com/Cheracpp/ChatNoJutsu.git
   ```

2. Configure Databases
    - Set up PostgreSQL and MongoDB
    - Update `application.properties` with your database connections

3. Build the project:
   ```bash
   mvn clean install
   ```

4. Run the application:
   ```bash
   mvn spring-boot:run
   ```

### Running Tests

Execute tests with:
```bash
mvn test
```