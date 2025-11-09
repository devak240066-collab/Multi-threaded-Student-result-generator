# Student Results Generator

Lightweight Spark (Java) web app for generating/importing/exporting student results with multithreading, CSV, and Excel export. Includes metrics and static UI.

## Run locally

- Requirements: Java 17+, Maven
- Start:
```
mvn package
java -jar target/student-results-1.0.0.jar
```
Open http://localhost:4567

## Docker
```
docker build -t student-results .
docker run -p 8080:8080 -e PORT=8080 -v %cd%/data:/app/data student-results
```
Open http://localhost:8080

## Render

- Push this repo to GitHub.
- Create new Web Service in Render from this repo, choose "Docker" environment.
- It will auto-detect Dockerfile. Health check: `/health`.
- Or use `render.yaml` in root (Infra as code). Render will set PORT; our app reads it.

## GitHub Actions

Basic CI builds the JAR and Docker image on push/PR. Add your Docker registry login steps if you wish to push images.

## API

- POST `/api/generate` JSON: `{ "count": 50, "subjects": ["Maths","English"] }`
- POST `/api/import` body: raw CSV text with headers `id,name,...`
- GET `/api/export` -> XLSX file
- GET `/api/metrics` -> JSON metrics
- GET `/api/students` -> JSON list

## CSV format
```
id,name,Maths,English,Science
S0001,John Doe,90,85,92
```

## Notes
- H2 file DB at `./data/studentdb` (mounted volume in Docker). Override with `JDBC_URL`.
- Thread pool size auto-scales by cores; metrics exposed at `/api/metrics`.
