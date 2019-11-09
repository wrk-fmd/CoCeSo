# Deployment with Docker

## Build Spring Boot applications

A central Dockerfile does the following build steps:
* Load and cache all maven dependencies.
  This cache only has to be reloaded when a POM file changes and can be reused after any other code change.
* Copy the source code (without `application.yml` files - see `.dockerignore` file).
* Build the module and its dependencies.
* Copy the built JAR file to a JRE-only container.
* Install optional additional dependencies in the JRE container.

The following parameters are used:
* `MODULE` (required): specifies the path to the module, relative to the base POM file.
* `APP` (required): specifies the name of the app JAR file (normally the artifact id, unless otherwise configured in the compile plugin).
* `INSTALL` (optional): specifies a space separated list of packages which should be installed using apt.

All Spring Boot applications can be built using Docker Compose, which gives the parameter values.
Docker Compose tags the images with `latest`.
This tag can be overridden by supplying the `COCESO_TAG` environment variable.

## Run Spring Boot application

Each application can be started by running the container.

The configuration is supplied by mounting it into `/config`.
The mounted directory defaults to the "normal" resources path of the module.
The directory can be overridden by supplying the `COCESO_*_CONFIG` environment variable for the module.

Additionally, the Spring profile `docker` is activated, i.e. the `application-docker.yml` file is loaded (if present).

Logs can be written to `/log`, which is bound to `./log` by default.
The log directory can be overridden by supplying the `COCESO_LOG` environment variable.

The services can communicate with each other using the service name as DNS name, which is resolved by Docker.
Any service ports that need to be accessed externally need to be bound in the Docker Compose `ports` property.

## Production deployment using systemd

A systemd unit definition is given in [`coceso.service`](coceso.service).
Put this file into `/etc/coceso` and create a symlink in `/etc/systemd/system/`.
Also, copy (or link) the [`docker-compose.yml`](../docker-compose.yml) file into `/etc/coceso`.
The environment definition is given [`environment`](environment).

Each service takes its configuration from a directory in `/etc/coceso/config/`.
Logging output is written to files in `/var/log/coceso` which must be created before the first run.

The systemd unit runs the services specified in Docker Compose using the `production` tag.
The images are not built automatically, i.e. you need to build them separately by running `sudo COCESO_TAG=production docker-compose build`.

After that, the services can be started using by `systemctl start coceso.service`.
Update the services by rebuilding the images and restarting the services using systemd.
