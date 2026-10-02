FROM eclipse-temurin:21-jdk AS build
WORKDIR /app

COPY mvnw pom.xml ./
COPY .mvn .mvn
RUN chmod +x mvnw && ./mvnw -q -B dependency:go-offline

COPY src src
RUN ./mvnw -q -B package -DskipTests

FROM eclipse-temurin:21-jre
WORKDIR /app

RUN useradd --system --no-create-home giacobello
COPY --from=build /app/target/*.jar app.jar
USER giacobello

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
