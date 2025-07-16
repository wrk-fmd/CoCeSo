FROM maven:3.9-eclipse-temurin-11-alpine AS build

COPY . /usr/src/mymaven/

WORKDIR /usr/src/mymaven
RUN cp \
    main/view/src/main/webapp/WEB-INF/classes/coceso.properties.docker \
    main/view/src/main/webapp/WEB-INF/classes/coceso.properties
RUN mvn -P tomcat10,-radio clean package

ADD https://dlcdn.apache.org/tomcat/jakartaee-migration/v1.0.9/binaries/jakartaee-migration-1.0.9-shaded.jar migration.jar
RUN java -jar migration.jar /usr/src/mymaven/main/view/target/coceso.war /usr/src/mymaven/main/view/target/coceso-migrated.war


FROM tomcat:10-jre11

COPY --from=build /usr/src/mymaven/main/view/target/coceso-migrated.war /usr/local/tomcat/webapps/coceso.war
