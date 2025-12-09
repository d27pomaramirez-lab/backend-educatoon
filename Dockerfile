
# 1. Etapa de construcción (Usa Maven y JDK 21)
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
# Compila y empaqueta el .jar (saltando los tests para hacerlo más rápido)
RUN mvn clean package -DskipTests

# 2. Etapa de ejecución (Usa una imagen ligera de Java 21)
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
# Copia el .jar generado en la etapa anterior
COPY --from=build /app/target/*.jar app.jar

# Expone el puerto 8080 (estándar de Spring Boot)
EXPOSE 8080

# Comando para iniciar la app
ENTRYPOINT ["java", "-jar", "app.jar"]