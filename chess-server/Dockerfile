FROM openjdk:17-alpine

RUN mkdir /logs
RUN find /logs -type d -exec chmod 0777 {} \;

RUN addgroup -S chess && adduser -S chess -G chess
USER chess:chess

ARG MAIN_JAR_FILE=build/*.jar
ARG PLAIN_JAR_LIBS_DIR=build/jars
ARG MODULE_LIBS_DIR=build/mods

COPY ${MAIN_JAR_FILE} /chess/app.jar
COPY ${PLAIN_JAR_LIBS_DIR}/*.jar /chess/lib/jars/
COPY ${MODULE_LIBS_DIR}/*.jar /chess/lib/mods/

ENTRYPOINT ["java", \
    "-classpath", "/chess/lib/jars/*", \
    "--module-path", "/chess/app.jar:/chess/lib/mods", \
    "--module", "com.pmarshall.chessgame.server/com.pmarshall.chessgame.server.Server" \
]