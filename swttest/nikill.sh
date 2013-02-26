#!/bin/bash
# A scriptet futtató felhasználó összes olyan Java alkalmazását bezárja,
# mely paraméterében szerepel a NativeInterface.
# Erre azért van szükség, mert van egy bug a DJ Native Swing projektben,
# ami miatt a Java alkalmazás végetérése után a hozzá tartozó natív peer
# nem áll le még kérésre sem és a grafikus felület lefagy, míg fut.
# A script elkerüli ezt az idegesítő hibát.
SUSER=${SUDO_USER}
[ -z "$SUSER" ] && SUSER=${USER}
for PID in $(ps aux | grep -v grep | grep "$SUSER" | grep java | grep NativeInterface | awk '{print $2}') ; do
    kill -9 "$PID" > /dev/null 2>&1
done
