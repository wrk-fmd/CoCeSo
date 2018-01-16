# Coordination Center Software

CoCeSo is a web application depending on a database which is used by ambulance services
for the management of its units at events (concerts, soccer matches,...).
It allows you to manage the status of units, create and track interventions and assign units to them.
A colorful interface facilitates an overview at a glance.
The interface language is German or English (with your contribution any other language can be added).

## Requirements

* Server
  * Java JRE (version 1.8 required)
  * Tomcat (version 8 required)
  * PostgreSQL DB (9.1 and newer tested)
  * Maven
* Client
  * Web Browser (full features only in Mozilla Firefox and Google Chrome tested)

## Build the project

* Build the project with Maven: `mvn package` in the top-most directory.

## Deployment

For the docker version see [docker/Readme.md](docker/Readme.md).

* Install Tomcat8
* Install Postgresql Database
* Create a Postgres user, that the webapplication will use
* Run the `create00.sql` and `create01_geocode.sql` script in `main/resources/sql/` to create the database tables (two separate databases `coceso` and `geocode` respectively)
* Create a first Administrator with the `create99_user.sql` script
* Deploy the war file
* Adapt the `coceso.properties` file in the deployed webapp

