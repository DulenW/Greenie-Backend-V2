# Greenie Backend

This is the backend service for the Greenie application, built using Spring Boot.

## Project Structure

```
Greenie-backend/
├── src/
│   ├── main/
│   │   ├── com/example/greeniebackend/
│   │   │   ├── config/                     # Configuration classes (security, CORS, etc.)
│   │   │   ├── controller/                 # REST controllers
│   │   │   ├── dto/                        # Data Transfer Objects
│   │   │   ├── entity/                     # JPA entities (models)
│   │   │   ├── exception/                  # Custom exceptions & handlers
│   │   │   ├── repository/                 # Spring Data JPA repositories
│   │   │   ├── security/                   # Security & authentication classes (JWT, OAuth)
│   │   │   ├── service/                    # Business logic & service layer
│   │   │   ├── utils/                      # Utility/helper classes
│   │   │   ├── ProjectApplication.java     # Main Spring Boot application file
│   │   ├── resources/
│   │   │   ├── static/                     # Static files (if needed)
│   │   │   ├── templates/                  # Templates (for email notifications, etc.)
│   │   │   ├── application.properties      # Main application properties
│   ├── test/
│   │   ├── com/example/greeniebackend/     # Unit & integration tests
│   │   │   ├── controller/                 # Controller tests
│   │   │   ├── service/                    # Service tests
│   │   │   ├── repository/                 # Repository tests
├── pom.xml                                 # Maven dependencies
├── .gitignore                              # Git ignore file
├── README.md                               # Documentation
```


