# Build stage
FROM maven:3.9.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn -q -e -B -DskipTests dependency:resolve
COPY src ./src
RUN mvn -q -e -B -DskipTests package

# Run stage
FROM eclipse-temurin:17-jre
WORKDIR /app
ENV PORT=8080
EXPOSE 8080
COPY --from=build /app/target/student-results-1.0.0.jar /app/app.jar
# For H2 file storage
VOLUME ["/app/data"]
CMD ["java","-jar","/app/app.jar"]
