FROM gradle:6.8.2-jdk11 AS builder
WORKDIR /home/gradle/test

COPY --chown=gradle:gradle build.gradle settings.gradle gradle ./
RUN gradle build --no-daemon > /dev/null 2>&1 || true

COPY --chown=gradle:gradle . ./
RUN gradle build --no-daemon -x test

FROM openjdk:16-ea-23-jdk-alpine3.12
RUN mkdir /myapp

RUN addgroup -g 1001 -S appuser && adduser -u 1001 -S appuser -G appuser
RUN chown -R appuser:appuser /myapp
USER appuser

COPY --from=builder /home/gradle/test/build/libs/*.jar /myapp/sba.jar

ENTRYPOINT [ "java", "-jar", "/myapp/sba.jar"]
