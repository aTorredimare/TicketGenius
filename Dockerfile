#FROM node:16.19 AS buildClient
#WORKDIR /client

#COPY ./client .
#RUN npm install --silent && npm run build

FROM gradle:7.6.1-jdk17 AS buildServer
WORKDIR /server
COPY ./ticketingServer .
#RUN mkdir -p ./src/main/resource/static
#COPY --from=buildClient /client/build/* ./src/main/resource/static
RUN gradle build -x test --no-daemon

FROM openjdk:17
WORKDIR /ticketing_server
EXPOSE 8081

ENV POSTGRES_USER=postgres
ENV POSTGRES_PWD=password
ENV KEYCLOAK_URL=http://keycloak:8080
ENV TEMPO_URL=http://tempo:9411
ENV LOKI_URL=http://loki:3100
COPY --from=buildServer ./server/build/libs/ticketingServer-0.0.1-SNAPSHOT.jar ./ticketingServer.jar
ENTRYPOINT ["java", "-jar", "/ticketing_server/ticketingServer.jar"]
