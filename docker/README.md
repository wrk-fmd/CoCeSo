# How to build and run CoCeSo using docker

This is a guide to waive the installation of dependencies and using docker instead.

Inspect the scripts in this directory for the actual shell commands. Later,
let's use docker compose.


## Requirements

* Server
  * Docker engine
* Client
  * Web Browser (full features only in Mozilla Firefox and Google Chrome tested)

## Build the project using a maven container

These steps build the deployable .war-file and two docker images.

* `./build` OR `./build_cached`, the later keeps the cache after building for faster re-builds.
* `./build_image` builds `coceso-app`.
* `./build_db` builds `coceso-db`.

## Deployment

Work in progress ...

* Create a Postgres user, that the webapplication will use
* Run the `create.sql` and `create_geocode.sql` script in `main/resources/sql/` to create the database tables (two separate tables)
* Create a first Administrator with the `create_user.sql` script
* Deploy the war file
* Adapt the `coceso.properties` file in the deployed webapp

