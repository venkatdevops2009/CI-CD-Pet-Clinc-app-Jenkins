# Pet Clinic Management System

A comprehensive Spring Boot web application for managing pet clinic operations, including owner profiles, pet health records, veterinarian management, and appointment scheduling.

## Features

✅ **Owner Management** - Register, edit, and manage pet owners  
✅ **Pet Registry** - Track pets with breed, type, and health history  
✅ **Health Records** - Comprehensive health tracking with vaccinations, treatments, and medications  
✅ **Veterinarian Profiles** - Manage vet profiles with specialties and experience  
✅ **Appointment Scheduling** - Book and track pet appointments  
✅ **Responsive UI** - Beautiful Bootstrap 5 interface with mobile support  
✅ **Data Validation** - Input validation and error handling  
✅ **Database Persistence** - MySQL backend with JPA/Hibernate  

## Tech Stack

- **Backend**: Spring Boot 3.1.5
- **Frontend**: Thymeleaf Templates + Bootstrap 5
- **Database**: MySQL 8.0+
- **ORM**: Spring Data JPA + Hibernate
- **Build**: Maven 3.8+
- **Java**: JDK 17+

## Prerequisites

### Required Software
- **Java 17+**: Download from [java.com](https://www.java.com)
- **Maven 3.8+**: Download from [maven.apache.org](https://maven.apache.org)
- **MySQL 8.0+**: Download from [mysql.com](https://www.mysql.com)
- **IDE**: IntelliJ IDEA, Eclipse, or VS Code with Java extensions

### Verify Installation
```bash
java -version
mvn --version
mysql --version
```

## Installation & Setup

### Step 1: Create Project Structure

```bash
# Navigate to your projects directory
cd d:\terraform practise

# Create Maven project
mvn archetype:generate ^
  -DgroupId=com.petclinic ^
  -DartifactId=petclinc-app ^
  -DarchetypeArtifactId=maven-archetype-quickstart ^
  -DinteractiveMode=false

cd petclinc-app

# Create directory structure
mkdir -p src\main\java\com\petclinic\{model,repository,service,controller}
mkdir -p src\main\resources\{templates,static\css}
mkdir -p src\test\java\com\petclinic
```

### Step 2: Copy Project Files

Copy the following files to their respective locations:

**pom.xml** → `petclinc-app/`

**Java Files** → `src/main/java/com/petclinic/`:
- Model Classes (entities) → `model/`
  - Owner.java, Pet.java, HealthRecord.java
  - Veterinarian.java, Appointment.java
  - PetType.java, Specialty.java
- Repository Classes → `repository/`
  - OwnerRepository.java, PetRepository.java, etc.
- Service Classes → `service/`
  - OwnerService.java, PetService.java, etc.
- Controller Classes → `controller/`
  - HomeController.java, OwnerController.java, etc.
- Application Class → `PetClinicApplication.java`

**Configuration** → `src/main/resources/`:
```
application.yml
```

**HTML Templates** → `src/main/resources/templates/`:
```
index.html
owners/
  - list.html
  - form.html
  - detail.html
pets/
  - list.html
  - form.html
  - detail.html
health-records/
  - list.html
  - form.html
vets/
  - list.html
  - detail.html
  - form.html
appointments/
  - list.html
  - form.html
```

### Step 3: Configure MySQL

**Create Database:**
```sql
CREATE DATABASE IF NOT EXISTS petclinic;
USE petclinic;
```

**Update application.yml** (in `src/main/resources/`):
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/petclinic?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=UTC
    username: root
    password: YOUR_MYSQL_PASSWORD
    driver-class-name: com.mysql.cj.jdbc.Driver
  
  jpa:
    hibernate:
      ddl-auto: update
    properties:
      hibernate:
        dialect: org.hibernate.dialect.MySQL8Dialect
        format_sql: true

server:
  port: 8080
```

### Step 4: Build & Run

**Install Dependencies:**
```bash
mvn clean install
```

**Run Application:**
```bash
mvn spring-boot:run
```

**Or using Java:**
```bash
mvn clean package
java -jar target/petclinc-app-1.0.0.jar
```

The application will start at: **http://localhost:8080**

## Usage Guide

### Dashboard
- View system statistics (total owners, pets, vets, appointments)
- Quick access to add new records

### Owners
- **Add Owner**: Click "Add Owner" button
- **View Owner**: Click "View" to see owner details and pets
- **Edit Owner**: Click "Edit" to modify information
- **Delete Owner**: Click "Delete" (deletes owner and associated pets)

### Pets
- **Add Pet**: Click "Add Pet" → select pet type and owner
- **View Pet Details**: See owner info, health records, and appointments
- **Update Pet**: Edit name, breed, color, DOB
- **Access Health Records**: Click "View All Records" from pet detail page

### Health Records (Core Feature)
- **Add Record**: Click "Add Record" or use pet detail page
- **Fill Details**:
  - Pet Selection
  - Record Type (Vaccination, Treatment, Checkup, etc.)
  - Record Date
  - Veterinarian
  - Description, Diagnosis, Treatment
  - Medication & Dosage
  - Additional Notes
- **View History**: See all records for a pet sorted by date
- **Edit/Delete**: Modify existing records

### Veterinarians
- **View Vets**: See all veterinarians with specialties
- **Add Vet**: Register new veterinarian
- **Add Specialties**: Link specialties to veterinarians

### Appointments
- **Book Appointment**: Select pet, vet, date/time, reason
- **View Upcoming**: See upcoming appointments
- **Update Status**: Mark as scheduled, completed, or cancelled
- **Manage Appointments**: Edit or cancel bookings

## API Endpoints

### Owners
- `GET /owners` - List all owners
- `GET /owners/{id}` - Get owner details
- `POST /owners` - Create new owner
- `POST /owners/{id}` - Update owner
- `POST /owners/{id}/delete` - Delete owner

### Pets
- `GET /pets` - List all pets
- `GET /pets/{id}` - Get pet details
- `GET /pets/owner/{ownerId}` - Pets of specific owner
- `POST /pets` - Create new pet
- `POST /pets/{id}` - Update pet
- `POST /pets/{id}/delete` - Delete pet

### Health Records
- `GET /health-records` - List all records
- `GET /health-records/pet/{petId}` - Records for specific pet
- `POST /health-records` - Add health record
- `POST /health-records/{id}` - Update record
- `POST /health-records/{id}/delete` - Delete record

### Veterinarians
- `GET /vets` - List all vets
- `GET /vets/{id}` - Get vet details
- `POST /vets` - Register new vet
- `POST /vets/{id}` - Update vet
- `POST /vets/{id}/delete` - Delete vet

### Appointments
- `GET /appointments` - List all appointments
- `GET /appointments/upcoming` - Upcoming appointments
- `POST /appointments` - Book appointment
- `POST /appointments/{id}` - Update appointment
- `POST /appointments/{id}/delete` - Cancel appointment
- `POST /appointments/{id}/status` - Update appointment status

## Database Schema

### Tables
- `owners` - Pet owner information
- `pet_types` - Types of pets (Dog, Cat, etc.)
- `pets` - Pet records with owner references
- `veterinarians` - Vet profiles and credentials
- `specialties` - Veterinary specialties
- `vet_specialties` - Join table for vet specialties
- `health_records` - Complete health history per pet
- `appointments` - Scheduled appointments

## Troubleshooting

### MySQL Connection Error
```
Solution: Verify MySQL is running and credentials in application.yml are correct
Windows: services.msc → MySQL8.0 → check if Running
```

### Port 8080 Already in Use
```
Solution: Change port in application.yml:
server:
  port: 8081
```

### Validation Errors
```
Solution: All required fields are marked with *. Ensure they are filled.
```

### Database Not Found
```
Solution: Ensure createDatabaseIfNotExist=true in JDBC URL (already set)
```

## File Structure

```
petclinc-app/
├── src/
│   ├── main/
│   │   ├── java/com/petclinic/
│   │   │   ├── PetClinicApplication.java
│   │   │   ├── model/
│   │   │   ├── repository/
│   │   │   ├── service/
│   │   │   └── controller/
│   │   └── resources/
│   │       ├── application.yml
│   │       ├── templates/
│   │       │   ├── index.html
│   │       │   ├── owners/
│   │       │   ├── pets/
│   │       │   ├── health-records/
│   │       │   ├── vets/
│   │       │   └── appointments/
│   │       └── static/
│   │           └── css/
│   └── test/
├── target/
├── pom.xml
└── README.md
```

## Development Tips

### Hot Reload (Development)
```bash
mvn spring-boot:run -Dspring-boot.run.arguments='--spring.devtools.restart.enabled=true'
```

### View Generated SQL
Enable in application.yml:
```yaml
spring:
  jpa:
    show-sql: true
```

### Database Migration
Tables are auto-created with `ddl-auto: update` - Hibernate generates schema from entities.

## Performance Optimization

- ✅ Entity relationships configured with lazy loading
- ✅ Indexed primary and foreign keys
- ✅ Service layer handles business logic
- ✅ Thymeleaf template caching enabled (production)

## Security Considerations

For production deployment:
1. Use environment variables for database credentials
2. Implement Spring Security for authentication
3. Add HTTPS/SSL configuration
4. Implement input sanitization
5. Add CSRF protection
6. Use password encryption for user accounts

## Contributing

Guidelines for extending the application:
1. Follow existing code structure
2. Use dependency injection
3. Add validation annotations for user input
4. Update both entity and template for new features
5. Test CRUD operations thoroughly

## License

This project is provided as-is for educational and commercial use.

## Support

For issues or questions:
1. Check Troubleshooting section
2. Verify all prerequisites are installed
3. Ensure MySQL is running and accessible
4. Review application logs in console

---

**Happy Pet Clinic Management! 🐾**

Built with ❤️ for pet lovers worldwide.
