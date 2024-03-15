FROM maven:3 AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

FROM tomcat:10.1-jdk11-corretto
COPY --from=build app/target/OpenWeather-1.0.0.war $CATALINA_HOME/webapps/openweather.war
CMD ["catalina.sh", "run"]