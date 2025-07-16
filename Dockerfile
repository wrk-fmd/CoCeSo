FROM maven:3.9-eclipse-temurin-11-alpine AS build

COPY . /usr/src/mymaven/

WORKDIR /usr/src/mymaven
RUN cp \
    main/view/src/main/webapp/WEB-INF/classes/coceso.properties.docker \
    main/view/src/main/webapp/WEB-INF/classes/coceso.properties
RUN mvn -P -radio clean package


FROM tomcat:9-jre11

COPY --from=build /usr/src/mymaven/main/view/target/coceso.war /usr/local/tomcat/webapps/
