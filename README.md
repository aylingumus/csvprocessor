# 🚀 CSV Processor Application

I built this simple Spring Boot application to help you upload, process, and manage medical records from a CSV file. I used an in-memory database for data storage and provided RESTful APIs for basic CRUD operations.

## 🤖 What can you do with this app?

* Upload a CSV file with medical records
* View all medical records
* Get a specific medical record by its unique code
* Delete all medical records

## 🛠️ Tech stuff

I implemented this app using the following technologies:

* Spring Boot for building RESTful web services
* H2 Database for development and testing
* OpenCSV for parsing CSV files
* ModelMapper for DTO to entity mapping
* JUnit & Mockito for unit and integration testing

## 🚀 Getting started

1. Clone the repository: `git clone https://github.com/aylingumus/csvprocessor`
2. Build the project using Maven: `mvn clean install`
3. Run the application: `mvn spring-boot:run`
4. Access the H2 database console at: `http://localhost:8080/h2-console`

## 🌐 API Endpoints

* `POST /api/medical-records/upload`: Upload a CSV file with medical records. You can use the `sample-data.csv` file in the `src/test/resources` directory to test this endpoint.
* `GET /api/medical-records`: Retrieve all medical records
* `GET /api/medical-records/{code}`: Get a medical record by its code
* `DELETE /api/medical-records`: Delete all medical records

## 🧪 Testing

I wrote unit and integration tests to ensure the app works as expected. You can run these tests with: `mvn test`

Enjoy! 😊