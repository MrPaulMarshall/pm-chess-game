FROM openjdk:17-alpine
RUN addgroup -S chess && adduser -S chess -G chess
USER chess:chess

ARG MAIN_JAR_FILE=target/*.jar
ARG LIBS_DIR=target/libs

COPY ${MAIN_JAR_FILE} /chess/app.jar
COPY ${LIBS_DIR}/*.jar /chess/libs/

ENTRYPOINT ["java","-cp","/chess/app.jar:/chess/libs/*","com.marshall.chessgame.server.Server"]