FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

COPY build/libs/encrypt-it.jar app.jar

RUN addgroup -S spring && adduser -S spring -G spring
RUN chown spring:spring app.jar

USER spring:spring

EXPOSE 8085

ENTRYPOINT ["java", "-jar", "app.jar"]