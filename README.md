# TilDau – AI-Powered Speech Therapy Platform

## Overview

TilDau is an AI-powered speech therapy platform designed to support speech development and pronunciation improvement for children. The system combines mobile learning, speech analysis, and artificial intelligence technologies to provide personalized exercises and automated feedback.

The platform enables users to complete speech therapy exercises, submit voice recordings, and receive detailed assessments based on pronunciation accuracy, fluency, and speech similarity metrics.


## Features

- User authentication and authorization
- Personalized speech therapy exercises
- Audio recording and submission
- Automatic speech recognition (ASR)
- Pronunciation assessment using Word Error Rate (WER)
- Fluency analysis based on speech tempo and pause detection
- Embedding-based speech similarity evaluation
- Progress tracking and exercise completion monitoring
- REST API for mobile application integration
- Containerized deployment using Docker

## System Architecture

The platform consists of three main components:

### Mobile Application
Provides the user interface for:
- User registration and login
- Exercise browsing
- Audio recording
- Progress tracking
- Receiving speech evaluation results

### Backend Service
Responsible for:
- Business logic
- User management
- Authentication and authorization
- Exercise management
- Communication with the AI module
- Data persistence

### AI Module
Responsible for speech analysis:
- Speech-to-text transcription
- Pronunciation evaluation
- Fluency assessment
- Semantic similarity analysis
- Generation of evaluation scores


## Technology Stack

### Backend
- Java 21
- Spring Boot
- Spring Security
- JWT Authentication
- PostgreSQL
- MinIO S3 strorage
- Maven

### AI Module
- Python 3.11+
- FastAPI
- OpenAI Whisper
- Sentence Transformers
- Librosa
- NumPy
- SciPy

### Mobile Application
- Android (Kotlin)
- Android SDK
- Gradle
- Jetpack Components
- Retrofit
- MediaRecorder
- MediaPlayer
- Material Design

### Infrastructure
- Docker
- Docker Compose
- Nginx Reverse Proxy
- Oracle Cloud Infrastructure (OCI)


## Speech Evaluation Pipeline

1. Audio recording is uploaded from the mobile application.
2. Backend forwards the audio file to the AI module.
3. AI module generates a transcription using Whisper.
4. Pronunciation quality is evaluated using Word Error Rate (WER).
5. Fluency metrics are calculated:
   - Speech tempo
   - Pause frequency
   - Pause duration
6. Semantic similarity is calculated using embedding vectors.
7. Final evaluation scores are returned to the backend.
8. Backend stores and returns results to the mobile application.


## Project Structure

```text
project-root/
│
├── backend/           # Spring Boot backend
│
├── ai-module/         # FastAPI AI service
│
├── mobile/            # Android application
│
├── nginx/             # Nginx reverse proxy configuration
│
├── data/              # Dataset for Embedding
│
├── ml/                # Embedding implementation
│
├── docker-compose.yml
│
└── README.md
```


## Getting Started

### Prerequisites

Make sure the following software is installed:

- Docker
- Docker Compose
- Git

Optional for local development:

- Java 21
- Maven
- Python 3.11+
- PostgreSQL


## Running with Docker

Clone the repository:

```bash
git clone <repository-url>
cd project-root
```

Build and start all services:

```bash
docker compose up --build
```

Run in detached mode:

```bash
docker compose up -d --build
```

Stop all services:

```bash
docker compose down
```

## Deployment

The platform is deployed using Docker containers on Oracle Cloud Infrastructure (OCI).

Production deployment includes:

- Nginx Reverse Proxy
- Spring Boot Backend Service
- FastAPI AI Module
- PostgreSQL Database
- MinIO S3 storage
- Docker Compose orchestration

Nginx serves as the public entry point and routes incoming requests to the appropriate backend services.

## Environment Configuration

Application configuration can be modified through environment variables or configuration files.

### Backend

Example configuration:

```properties
spring.datasource.url=jdbc:postgresql://postgres:5432/tildau
spring.datasource.username=postgres
spring.datasource.password=password

ai.service.url=http://ai:8000
```

### AI Module

Example configuration:

```env
MODEL_NAME=whisper-base
```


## API Communication

```text
Mobile App
     │
     ▼
Nginx Reverse Proxy
     │
     ▼
Backend (Spring Boot)
     │
     ▼
AI Module (FastAPI)
     │
     ▼
Speech Evaluation Results
```


## Future Improvements

- Advanced machine learning models
- Therapist dashboard
- Multi-language support
- Personalized learning recommendations


## License

This project was developed for academic and educational purposes.

All rights reserved.