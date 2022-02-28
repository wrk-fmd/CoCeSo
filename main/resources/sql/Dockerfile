FROM postgres:9.6-alpine

COPY /create*.sql /docker-entrypoint-initdb.d/

# providing default credentials - INSECURE
ENV \
    POSTGRES_PASSWORD=password \
    POSTGRES_USER=coceso
