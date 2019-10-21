#!/bin/bash

( socat -d -d pty,raw,echo=0,link=/dev/ttyUSB50 pty,raw,echo=0,link=/dev/ttyIN50 ) &
( socat -d -d pty,raw,echo=0,link=/dev/ttyUSB51 pty,raw,echo=0,link=/dev/ttyIN51 ) &

sleep 3
chmod 666 /dev/ttyUSB50 /dev/ttyUSB51

while true ; do
  if [ $RANDOM -le 3000 ]; then
    echo -ne "\x02E3456789\x03" > /dev/ttyIN50
  else
    echo -ne "\x02I13456789\x03" > /dev/ttyIN50
  fi

  sleep $((10+RANDOM/6000))

  if [ $RANDOM -le 3000 ]; then
    echo -ne "\x02E1234567\x03" > /dev/ttyIN51
  else
    echo -ne "\x02I11234567\x03" > /dev/ttyIN51
  fi

  sleep $((30+RANDOM/3000))
done;
