# AWS S3 Spring Boot Integration

A Spring Boot web application demonstrating Amazon S3 operations through a responsive UI. Supports bucket creation, file uploads, downloads, and deletions using AWS SDK v2.

---

## AWS Setup Guide

Before running this application, set up AWS S3 access and credentials.

**Complete setup instructions: [AWS S3 Setup Guide](AWS-S3-Guide.md)**

**The guide covers:**
*   What is Amazon S3 and its use cases
*   IAM User creation and permissions
*   Access key generation
*   S3 bucket creation (manual setup)
*   Bucket policies for public access (optional)
*   Environment configuration
*   Troubleshooting common issues

---

## Features

*   Create S3 buckets dynamically
*   Upload files to any bucket
*   Download files from S3
*   Delete files programmatically

**Access**: `http://localhost:8080`

---

## Architecture

**Backend** (Spring Boot):
*   `AwsConfig.java` - Initializes S3 client with credentials
*   `AwsS3Service.java` - Handles S3 operations (create, upload, download, delete)
*   `AwsS3Controller.java` - REST API endpoints at `/api/s3/*`
*   `AWSS3Application.java` - Main application with .env loader

**Frontend** (HTML/CSS/JS):
*   `index.html` - UI with 4-card grid layout
*   `style.css` - Responsive styling
*   `script.js` - Axios-based API calls

**API Endpoints:**
*   `POST /api/s3/create-bucket/{bucketName}`
*   `POST /api/s3/upload`
*   `GET /api/s3/download/{bucketName}/{fileName}`
*   `DELETE /api/s3/delete/{bucketName}/{fileName}`

---

## Local Setup

**1. Clone the repository**
```bash
git clone <repository-url>
cd aws-s3-spring-boot
```

**2. Configure environment variables**

Follow the **[AWS S3 Setup Guide](AWS-S3-Guide.md)** to get credentials, then:
```bash
cp .env.example .env
```

Edit `.env` with your AWS credentials:
```env
AWS_S3_ACCESS_KEY=your-access-key
AWS_S3_SECRET_KEY=your-secret-key
AWS_S3_REGION=us-west-1
```

**3. Build and run**
```bash
./mvnw clean install
./mvnw spring-boot:run
```

Access at `http://localhost:8080`

---

## Usage

**Create Bucket**: Enter a globally unique bucket name and click Create Bucket.

**Upload File**: Select bucket name, choose file, and click Upload File.

**Download File**: Enter bucket name and file name, click Download File.

**Delete File**: Enter bucket name and file name, click Delete File.

---

## Tech Stack

*   Spring Boot 3.5.9, Java 17, Maven
*   AWS SDK v2.29.3, dotenv-java 3.0.0
*   Frontend: HTML5, CSS3, Axios

---

## Troubleshooting

See **[AWS S3 Setup Guide - Troubleshooting](AWS-S3-Guide.md#8-troubleshooting)** for detailed solutions.

*   **Access Denied**: Check credentials and IAM permissions
*   **Bucket exists**: Use a different bucket name
*   **Region mismatch**: Match `.env` region with bucket region
*   **Build fails**: Run `./mvnw clean install`
