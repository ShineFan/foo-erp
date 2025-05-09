# Foo ERP System

A modern Enterprise Resource Planning (ERP) system built with Spring Boot and ShardingSphere.

## Features

- User management with role-based access control
- Database sharding support using ShardingSphere
- Distributed session management with Redis
- RESTful API design
- Modern web interface with Thymeleaf

## Technology Stack

- Java 17
- Spring Boot 3.x
- MyBatis
- ShardingSphere
- MySQL
- Redis
- Thymeleaf

## Getting Started

### Prerequisites

- JDK 17 or later
- Maven 3.6 or later
- MySQL 8.0 or later
- Redis 6.0 or later

### Installation

1. Clone the repository:
```bash
git clone https://github.com/yourusername/foo-erp.git
cd foo-erp
```

2. Configure the database:
- Create a MySQL database named `foo_erp`
- Update the database configuration in `src/main/resources/application.properties`

3. Build the project:
```bash
mvn clean install
```

4. Run the application:
```bash
mvn spring-boot:run
```

The application will be available at `http://localhost:8080`

## Database Sharding

The system uses ShardingSphere for database sharding. Currently, the following tables are sharded:
- users
- users_roles

Other tables use the default data source without sharding.

## License

This project is licensed under the MIT License - see the LICENSE file for details.
