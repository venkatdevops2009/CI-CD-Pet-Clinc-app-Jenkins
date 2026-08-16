# CI/CD Pet Clinic Application

This repository contains a Spring Boot Pet Clinic application, a Jenkins-based CI/CD pipeline, Docker packaging, and a minimal Terraform foundation for AWS infrastructure. The project is designed to demonstrate a DevSecOps workflow: build, test, static analysis, security scanning, image packaging, and deployment automation.

## What is in this repository

- `petclinc/` — Spring Boot application source code and Maven build
- `mysql/` — hardened MySQL container image used by the app database
- `Jenkinsfile` — pipeline that runs Maven, SonarQube, Trivy, Docker, and ECR steps
- `docker-compose.yaml` — local container stack for app and MySQL
- `.env.example` — local environment variables for Docker Compose
- `Infra-Jenkins/` — Terraform layers for the VPC and security group foundation
- `images/` — screenshots for application, Jenkins, Sonar, and Trivy outputs
- `sonar-issues.json` — exported Sonar issues used during remediation work
- `sonar-project.properties` — Sonar project configuration

## Features

- Owner management with add, view, edit, and delete flows
- Pet registration linked to an owner
- Health record tracking for pets
- Veterinarian catalog and related workflows
- Appointment booking and management
- Thymeleaf + Bootstrap user interface
- MySQL persistence via Spring Data JPA
- Input validation and form handling
- CI/CD pipeline with Maven, SonarQube, Trivy, Docker, and ECR support
- JaCoCo coverage enforcement for build quality

## Tech stack

- Java 17
- Spring Boot 3.5.14
- Spring Data JPA
- Thymeleaf
- MySQL 8
- Maven
- Docker
- Jenkins
- SonarQube
- Trivy
- Amazon ECR
- JUnit 5 / Mockito
- JaCoCo

## Repository layout

```text
CI-CD-Pet-Clinc-app-Jenkins/
├── README.md
├── Jenkinsfile
├── docker-compose.yaml
├── .env.example
├── sonar-issues.json
├── sonar-project.properties
├── images/
├── mysql/
│   └── Dockerfile
├── petclinc/
│   ├── Dockerfile
│   ├── pom.xml
│   └── src/
├── Infra-Jenkins/
│   ├── 00-vpc/
│   │   └── README.md
│   └── 10-sg/
│       └── README.md
└── .gitignore
```

## Prerequisites

- Java 17+
- Maven 3.9+
- Docker and Docker Compose
- MySQL or Docker Compose stack
- Jenkins with pipeline support and required plugins such as:
  - Pipeline
  - AnsiColor
  - AWS Credentials
  - SonarQube Scanner
- SonarQube server configured as `sonar-server`
- Trivy installed on the agent host

## Local development

### 1. Clone the repository

```bash
git clone <repo-url>
cd CI-CD-Pet-Clinc-app-Jenkins
```

### 2. Build the app

```bash
cd petclinc
mvn clean verify
```

### 3. Run the app locally

```bash
cd petclinc
mvn spring-boot:run
```

Application URL:

```text
http://localhost:8080
```

### 4. Run with Docker Compose

```bash
docker-compose up --build
```

This starts the application and MySQL containers using the values from `.env.example`.

## CI/CD flow

The `Jenkinsfile` performs the following stages:

1. Checkout source code
2. Read version information from `petclinc/pom.xml`
3. Run Maven build and unit tests
4. Verify generated build artifacts
5. Publish test results
6. Run SonarQube analysis
7. Enforce the quality gate
8. Run Trivy filesystem scan
9. Build Docker images for the app and MySQL database
10. Run Trivy image scans
11. Push images to Amazon ECR
12. Optionally deploy using the `DEPLOY` and environment parameters

### Jenkins expectations

The pipeline assumes:

- A SonarQube server named `sonar-server`
- AWS credentials with ID `aws-creds`
- Jenkins agent label `roboshop`
- ANSI color plugin for readable console output

## Code quality and security checks

### SonarQube

```bash
cd petclinc
mvn sonar:sonar
```

### Trivy

```bash
trivy fs --scanners vuln,secret,misconfig --severity HIGH,CRITICAL .
```

The repo also contains a quality gate in Maven and a dedicated Sonar export file (`sonar-issues.json`) for issue tracking and remediation.

## Terraform infrastructure foundation

The `Infra-Jenkins` folder contains the AWS foundation for this project:

- `00-vpc` — creates the VPC, subnets, gateways, and route tables
- `10-sg` — creates the base security groups used by the environment

These modules are the infrastructure starting point for the PetClinic deployment and are intended to be used before additional AWS resources are added.

## Notes

- `sonar-issues.json` documents the Sonar findings that were investigated and addressed during hardening work.
- The application container is designed for a hardened runtime with non-root execution and reduced asset exposure.
- The repository reflects a practical example of applying security scanning, code-quality checks, and container hardening in a Java application workflow.

## License

This project is provided for learning, demonstration, and DevSecOps practice purposes.
