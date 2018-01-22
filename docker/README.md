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

NOTE: The result differs from the pristine build in two ways.

* It uses hard-coded database credentials - INSECURE.
* It does not compile the radio pugin because of missing dependencies.

These steps build the deployable .war-file and two docker images.

* Copy `main/view/src/main/webapp/WEB-INF/classes/coceso.properties.docker` to `coceso.properties` in that directory.
* `./build_war` builds the deployable; optional parameters are
	* `./build_war_cached` (alternative command) keeps the cache after building for faster re-builds.
	* `-Drequirejs.optimize.skip=true` skips lengthy uglify.
	* `clean` removes artefacts before compiling and packaging.
* `./build_app` builds `coceso-app`.
* `./build_db` builds `coceso-db`.

## Deployment

Run the following commands. Coceso is then available at http://localhost:8080.

	docker network create coceso
	docker run -d --network coceso --name coceso-db coceso-db
	docker run -d --rm --network coceso -p 8080:8080 --name coceso-app coceso-app

Note that you likely want to keep the container `coceso-db`, but not necessarily `coceso-app`.
