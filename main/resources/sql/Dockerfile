FROM postgres:9.6-alpine

COPY /create*.sql /docker-entrypoint-initdb.d/
RUN chmod 644 /docker-entrypoint-initdb.d/create*.sql

