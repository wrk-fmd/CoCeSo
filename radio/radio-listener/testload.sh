#!/bin/bash

( socat -d -d pty,raw,echo=0,link=/dev/ttyUSB60 pty,raw,echo=0,link=/dev/ttyIN60 ) &

sleep 3
chmod 666 /dev/ttyUSB60

curl -X POST http://localhost:8090/radio/ports/reload

for i in $(seq -w 9999999); do
  echo -ne "\x02I1$i\x03" > /dev/ttyIN60
  sleep 0.05
done
