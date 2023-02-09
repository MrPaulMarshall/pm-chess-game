FROM openjdk:17-alpine
RUN addgroup -S chess && adduser -S chess -G chess
USER chess:chess

ARG MAIN_JAR_FILE=target/*.jar
ARG PLAIN_JAR_LIBS_DIR=target/lib/plain-jars
ARG MODULE_LIBS_DIR=target/lib/modules

COPY ${MAIN_JAR_FILE} /chess/app.jar
COPY ${PLAIN_JAR_LIBS_DIR}/*.jar /chess/lib/plain-jars/
COPY ${MODULE_LIBS_DIR}/*.jar /chess/lib/modules/

ENTRYPOINT ["java", \
    "-classpath", "/chess/lib/plain-jars/*", \
    "--module-path", "/chess/app.jar:/chess/lib/modules", \
    "--module", "com.pmarshall.chessgame.server/com.pmarshall.chessgame.server.Server" \
]