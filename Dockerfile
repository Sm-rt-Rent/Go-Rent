# Stage 1: Build the application
FROM maven:3.8.4-openjdk-17-slim AS build
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline -B
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Run the application
FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app
RUN addgroup -S gorent && adduser -S gorent -G gorent
COPY --from=build /app/target/*.jar app.jar
RUN chown gorent:gorent app.jar
USER gorent
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]







## Step 1: Use a lightweight Java Runtime
#FROM eclipse-temurin:17-jdk-alpine
#
## Step 2: Create a directory for the app
#WORKDIR /app
#
## Step 3: Copy the JAR file from your target folder to the container
## Note: Ensure you run 'mvn clean package' before building the docker image
#COPY target/*.jar app.jar
#
## Step 4: Expose the port SmartRent runs on
#EXPOSE 8080
#
## Step 5: Run the application
#ENTRYPOINT ["java", "-jar", "app.jar"]

