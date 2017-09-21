# Nginx Configuration

> This documentation is copied from the previous version of the repository.
> There will be an English translation as soon as possible.

Für den Zugriff auf Coceso mittels Nginx als Proxy sowie Auslieferung zusätzlicher Dateien direkt durch Nginx.
Alle relativen Pfade beziehen sich auf das Konfigurationsverzeichnis von Nginx (unter Debian `/etc/nginx/`)
Konfigurationsdatei unter sites-available (mit Symlink in sites-enabled).
Die SSL-Zertifikate müssen mit dieser Konfiguration unter ssl liegen.

```
server {
  listen 80;
  server_name coceso;

  access_log /var/log/nginx/coceso_access.log combined;
  error_log /var/log/nginx/coceso_error.log warn;

  return 301 https://$host$request_uri;
}

server {
  listen 443;
  server_name coceso;

  access_log /var/log/nginx/coceso_ssl_access.log combined;
  error_log /var/log/nginx/coceso_ssl_error.log warn;

  ssl on;
  ssl_certificate ssl/coceso.crt;
  ssl_certificate_key ssl/coceso.key;

  root /var/www/coceso;

  rewrite ^/$ $scheme://$host/coceso/ permanent;

  location /coceso/ {
    proxy_pass http://127.0.0.1:8080;
    proxy_http_version 1.1;
    proxy_set_header Upgrade $http_upgrade;
    proxy_set_header Connection $connection_upgrade;
  }
}
```

Zusätzlich wird für die Websockets unter `conf.d/websocket.conf` benötigt:

```
map $http_upgrade $connection_upgrade {
  default upgrade;
  ''      close;
}
```
