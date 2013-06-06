#!/bin/bash
# Konzolból futtatható telepítő Linux operációs-rendszer alá.

# Ha a dialog parancs nincs telepítve, kilépés
hash dialog 2>/dev/null || { echo >&2 "I require 'dialog' but it's not installed.  Aborting."; exit 1; }

# a felhasználónév megszerzése
SUSER=${SUDO_USER}
[ -z "$SUSER" ] && SUSER=${USER}

# konfiguráció
TMP_DIR="/tmp/mobilerc-installer-$SUSER"
TMP_DIR_NAME="$TMP_DIR/dir_name"
TMP_ERROR="$TMP_DIR/error"
DEF_DIR_NAME="Mobile-RC"
TITLE="Mobile-RC Telepítő ($SUSER)"

# tmp könyvtár létrehozása
mkdir -p "$TMP_DIR"

if [ -f "$TMP_DIR_NAME" ] ; then
    # ha létezik a könyvtárnevet tároló fájl, érték kiolvasása
    DIR_NAME=`cat "$TMP_DIR_NAME"`
    # érték trimmelése
    DIR_NAME=`echo "$DIR_NAME" | awk '{sub(/^ */,"",$1); sub(/ *$/,"",$1); print $1}'`
    # ha az érték null vagy üres string, default érték használata
    [ -z "$DIR_NAME" ] && DIR_NAME="$DEF_DIR_NAME"
else
    # ha nem létezik a könyvtárnevet tároló fájl, default érték használata
    DIR_NAME="$DEF_DIR_NAME"
fi

# üdvözlőképernyő és könyvtárnév megadó dialógus megjelenítése
dialog --clear --backtitle "$TITLE" --inputbox  \
"Üdvözli a Mobile-RC telepítő!\n\n
Az alkalmazás a CD-ről az ön felhasználói könyvtárába fog feltelepülni.\n\n
Ha a könyvtár már létezik, akkor telepítés helyett eltávolítás fog lefutni.\n\n
Kérem, adja meg a használandó könyvtár nevét:" 16 60 "$DIR_NAME" 2> "$TMP_DIR_NAME"

# ha valamiért a dialógus nem jeleníthető meg, tmp könyvtár törlése és kilépés
if [ $? -ne 0 ] ; then
    rm -rf $TMP_DIR
    exit 1
fi

# a megadott könyvtárnév beolvasása
DIR_NAME=`cat "$TMP_DIR_NAME"`

# ha kilépésre mentek vagy üres könyvtárnevet adtak meg, script vége
if [ -z "$DIR_NAME" ] ; then
    clear
    exit 0
fi

# az a könyvtár, ahonnan fut a telepítő
CURR_DIR=`dirname $0`

# útvonal megszerzése a könyvtárhoz
USER_HOME=$(eval echo ~"$SUSER")
DIR_NAME=`echo "$DIR_NAME" ${USER_HOME} | awk '{sub(/^ */,"",$1); sub(/ *$/,"",$1); print $2"/"$1}'`

# ha létezik a könyvtár, megkérdi legyen-e eltávolítás
REMOVING=0
if [ -d "$DIR_NAME" ] ; then
    dialog --clear --backtitle "$TITLE" --yes-label "Újratelepítés" --no-label "Törlés" \
    --title "Létező könyvtár" --yesno "Újratelepíti vagy törli az alkalmazást?" 5 60
    [ $? -eq 1 ] && REMOVING=1
fi

# az ikonokat tartalmazó könyvtár megszerzése
ICON_PATH="$USER_HOME/.local/share/icons"

# az indítófájlok neve
BRIDGE_NAME="mobilerc-bridge"
CONTROLLER_NAME="mobilerc-controller"

# a rendszeren használt ikonnevek
BRIDGE_ICON_NAME="$BRIDGE_NAME.png"
CONTROLLER_ICON_NAME="$CONTROLLER_NAME.png"

# az alkalmazásindító fájlok könyvtára
APPLICATIONS_DIR="$USER_HOME/.local/share/applications"

# a függvény megadja egy indítófájl helyét
getEntryLoc() {
    echo -n "$APPLICATIONS_DIR/$1.desktop"
}

# az indítófájlok helye
BRIDGE_ICON_FILE=$(getEntryLoc "$BRIDGE_NAME")
CONTROLLER_ICON_FILE=$(getEntryLoc "$CONTROLLER_NAME")

# az asztal útvonalának kiderítése
: ${XDG_CONFIG_HOME:=~/.config}
[ -f "${XDG_CONFIG_HOME}/user-dirs.dirs" ] && . "${XDG_CONFIG_HOME}/user-dirs.dirs"
DESKTOP_DIR="${XDG_DESKTOP_DIR:-~/Desktop}"

if [ $REMOVING -eq 0 ] ; then

###############
# TELEPÍTŐ ÁG #
###############

# könyvtár törlése, ha létezik, hátha nem üres
rm -rf "$DIR_NAME" > /dev/null 2>&1

# könyvtár létrehozása
mkdir -p "$DIR_NAME"/lib

# ha nem sikerült létrehozni, hibaüzenet és kilépés
if [ $? -ne 0 ] ; then
    dialog --clear --backtitle "$TITLE" --title "Hiba" --msgbox "A telepítőnek nincs jogosultsága a megadott könyvtár létrehozásához. A telepítő kilép!" 6 60
    clear
    exit 1
fi

# mégkérdi, legyenek-e telepítve a teszt tanúsítványok
dialog --clear --backtitle "$TITLE" --title "Teszt tanúsítványok" --yesno \
"A CD-n szerepelnek olyan teszt tanúsítványok, melyekkel a program gyorsan kipróbálható anélkül, hogy tanúsítványokat kelljen generálni.\n\n
Tesztelésre ajánlott a használatuk, de az éles rendszereken ne használja őket!\n\n
Szeretné telepíteni a teszt tanúsítványokat?" 12 60

# a válasz mentése változóba
SAMPLE=$?

# az alap fájl-lista
FILES=('bin/ui-outer.jar' 'bin/client.sh' 'bin/server.sh' 'desktop/BrowserTest/lib/swt/swt-linux32-4.3M5a.jar' 'desktop/BrowserTest/lib/swt/swt-linux64-4.3M5a.jar')
FILES_TO=('ui.jar' 'client.sh' 'server.sh' 'lib/swt-linux32.jar' 'lib/swt-linux64.jar')

# ha kellenek a teszt tanúsítványok is, a könyvtár és a konfigok hozzáadása a listához
if [ $SAMPLE -eq 0 ] ; then
    FILES+=('test-certs-passwd')
    FILES_TO+=('test-certs-passwd')
    FILES+=('bridge.conf')
    FILES_TO+=('bridge.conf')
    FILES+=('controller.ser')
    FILES_TO+=('controller.ser')
fi

# a fájl-lista mérete
COUNT=${#FILES[*]}

# törli a hibát tároló fájlt, ha létezik
[ -f "$TMP_ERROR" ] && rm "$TMP_ERROR"

# fájlmásoló dialógus indítása
COUNTER=0
(
while :
do

# fájl útvonal
FILE_NAME=${FILES[$COUNTER]}
# innen másol
SRC_FILE="$CURR_DIR/$FILE_NAME"
# mostantól csak fájlnév
FILE_NAME=${FILES_TO[$COUNTER]}
# ide másol
DST_FILE="$DIR_NAME/$FILE_NAME"

cat <<EOF
XXX
$(($COUNTER * 100 / $COUNT))
Fájl másolása: $FILE_NAME
XXX
EOF

# fájl másolása
cp -R $SRC_FILE $DST_FILE

# ha nem sikerült a fájl másolása, fájlnév mentése és kilépés az alprogramból
if [ $? -ne 0 ] ; then
    echo "$FILE_NAME" > "$TMP_ERROR"
    exit 1
fi

# teszt késleltetés
sleep 0.2

# számláló növelése és kilépés a ciklusból, ha nagyobb vagy egyenlő 100-nál
(( COUNTER+=1 ))
[ $COUNTER -ge $COUNT ] && break

done
) |
dialog --clear --backtitle "$TITLE" --title "Telepítés" --gauge "Kérem várjon" 7 60 0

# az alprogramban történt hiba kiolvasása
ERROR=""
[ -f "$TMP_ERROR" ] && ERROR=`cat "$TMP_ERROR"`

# ha van hiba
if [ -n "$ERROR" ] ; then
    # hiba jelzése
    dialog --clear --backtitle "$TITLE" --title "Hiba" --msgbox "Nem sikerült az alábbi fájl másolása:\n$ERROR" 6 60
    # megkérdi, törölje-e a teljes könyvtárat
    dialog --clear --backtitle "$TITLE" --title "Telepítés vége" --yesno "A telepítés megszakadt!\n\nTöröljem a teljes könyvtárat?\n$DIR_NAME" 8 60
    # ha igen, rekurzív törlés és esetleges hibák figyelmen kívül hagyása
    [ $? -eq 0 ] && rm -rf "$DIR_NAME" > /dev/null 2>&1
    # kilépés
    clear
    exit 1
fi

# futási jogot ad a bash scriptekre
chmod u+x "$DIR_NAME/client.sh"
chmod u+x "$DIR_NAME/server.sh"

# az asztal könyvtár létrehozása, ha nem létezik
mkdir -p "$DESKTOP_DIR"

# az ikonokat tartalmazó könyvtár létrehozása, ha nincs még létrehozva
mkdir -p "$ICON_PATH"

# a menükönyvtár létrehozása, ha nem létezik
mkdir -p "$APPLICATIONS_DIR"

# a függvény megadja egy indítófájl teljes szövegét
getEntryText() {
echo -n "
#!/usr/bin/env xdg-open\n\n[Desktop Entry]\nName=$1\nName[hu]=$2\nComment=$3\nComment[hu]=$4\nExec=$DIR_NAME/$5\nIcon=$ICON_PATH/$6\nStartupNotify=False\nTerminal=False\nType=Application\nCategories=Network"
}

# az ikonfájlok tartalma
BRIDGE_ICON_TEXT=$(getEntryText "Mobil-RC Bridge" "Mobile-RC Híd" "The server application" "A szerver alkalmazás" "server.sh" "$BRIDGE_ICON_NAME")
CONTROLLER_ICON_TEXT=$(getEntryText "Mobil-RC controller" "Mobile-RC vezérlő" "The client application" "A kliens alkalmazás" "client.sh" "$CONTROLLER_ICON_NAME")

# menüopciók létrehozása
echo -n -e "$BRIDGE_ICON_TEXT" > "$BRIDGE_ICON_FILE"
echo -n -e "$CONTROLLER_ICON_TEXT" > "$CONTROLLER_ICON_FILE"

# futási jog a menüopcióra
chmod u+x "$BRIDGE_ICON_FILE"
chmod u+x "$CONTROLLER_ICON_FILE"

# szimbólikus link az asztalra
ln -s "$BRIDGE_ICON_FILE" "$DESKTOP_DIR" > /dev/null 2>&1
ln -s "$CONTROLLER_ICON_FILE" "$DESKTOP_DIR" > /dev/null 2>&1

# ikonfájlok másolása
cp "$CURR_DIR/src/org/dyndns/fzoli/rccar/controller/resource/icon.png" "$ICON_PATH/$CONTROLLER_ICON_NAME"
cp "$CURR_DIR/src/org/dyndns/fzoli/rccar/bridge/resource/bridge.png" "$ICON_PATH/$BRIDGE_ICON_NAME"

# telepítés vége üzenet
dialog --clear --backtitle "$TITLE" --msgbox "A telepítés végetért." 5 60

else

#################
# ELTÁVOLÍTÓ ÁG #
#################

# az összes futó alkalmazás kilövése
for PID in $(ps aux | grep -v grep | grep "$SUSER" | grep java | grep ui.jar | awk '{print $2}') ; do
    kill -9 "$PID" > /dev/null 2>&1
done

# az egész könyvtár törlése
rm -rf $DIR_NAME > /dev/null 2>&1

# a program által létrehozott konfig könyvtár törlése, hiba figyelmen kívül hagyása
rm -rf "$USER_HOME/.config/Mobile-RC" > /dev/null 2>&1

# az asztalon lévő ikonok törlése, hiba figyelmen kívül hagyása
rm -f "$DESKTOP_DIR/$BRIDGE_NAME.desktop" > /dev/null 2>&1
rm -f "$DESKTOP_DIR/$CONTROLLER_NAME.desktop" > /dev/null 2>&1

# a menüben lévő ikonok törlése, hiba figyelmen kívül hagyása
rm -f "$BRIDGE_ICON_FILE" > /dev/null 2>&1
rm -f "$CONTROLLER_ICON_FILE" > /dev/null 2>&1

# az ikon-képfájlok törlése
rm -f "$ICON_PATH/$CONTROLLER_ICON_NAME" > /dev/null 2>&1
rm -f "$ICON_PATH/$BRIDGE_ICON_NAME" > /dev/null 2>&1

# törlés vége üzenet
dialog --clear --backtitle "$TITLE" --msgbox "Az eltávolítás végetért." 5 60

fi

# kilépés előtt a képernyő törlése
clear
