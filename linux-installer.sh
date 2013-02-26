#!/bin/bash
# EGYELŐRE CSAK TESZT!

# a felhasználónév megszerzése
SUSER=${SUDO_USER}
[ -z "$SUSER" ] && SUSER=${USER}

# konfiguráció
TMP_DIR=/tmp/mobilerc-installer-$SUSER
TMP_DIR_NAME=$TMP_DIR/dir_name
TMP_ERROR=$TMP_DIR/error
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

# könyvtár létrehozása, ha nem létezik
mkdir -p $DIR_NAME

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
FILES=('swttest/ui.jar' 'swttest/client.sh' 'swttest/server.sh')

# ha kellenek a teszt tanúsítványok is, a könyvtár és a konfigok hozzáadása a listához
if [ $SAMPLE -eq 0 ] ; then    
    FILES+=('test-certs-passwd')    
    FILES+=('bridge.conf')
    FILES+=('controller.ser')
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
FILE_NAME=$(basename "$FILE_NAME")
# ide másol
DST_FILE="$DIR_NAME/$FILE_NAME"

cat <<EOF
XXX
$(($COUNTER * 100 / $COUNT))
Fájl másolása: $FILE_NAME
XXX
EOF

# fájl másolása
cp -r $SRC_FILE $DST_FILE

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

# az ikonokat tartalmazó könyvtár megszerzése, ha nincs létrehozása
ICON_PATH="$USER_HOME/.local/share/icons"
mkdir -p "$ICON_PATH"

# a függvény megadja egy indítófájl teljes szövegét
getEntryText() {
echo -n "
#!/usr/bin/env xdg-open\n\n[Desktop Entry]\nName=$1\nName[hu]=$2\nComment=$3\nComment[hu]=$4\nExec=$DIR_NAME/$5\nIcon=$ICON_PATH/$6\nStartupNotify=True\nTerminal=False\nType=Application\nCategories=Network"
}

# a függvény megadja egy indítófájl helyét
getEntryLoc() {
echo -n "$USER_HOME/.local/share/applications/$1.desktop"
}

# a rendszeren használt ikonnevek
BRIDGE_ICON_NAME="mobilerc-bridge.png"
CONTROLLER_ICON_NAME="mobilerc-controller.png"

# az ikonfájlok tartalma
BRIDGE_ICON_TEXT=$(getEntryText "Mobil-RC Bridge" "Mobile-RC Híd" "The server application" "A szerver alkalmazás" "server.sh" "$BRIDGE_ICON_NAME")
CONTROLLER_ICON_TEXT=$(getEntryText "Mobil-RC controller" "Mobile-RC vezérlő" "The client application" "A kliens alkalmazás" "client.sh" "$CONTROLLER_ICON_NAME")

# az indítófájlok helye
BRIDGE_ICON_FILE=$(getEntryLoc "mobilerc-bridge")
CONTROLLER_ICON_FILE=$(getEntryLoc "mobilerc-controller")

# menüopciók létrehozása
echo -n -e "$BRIDGE_ICON_TEXT" > "$BRIDGE_ICON_FILE"
echo -n -e "$CONTROLLER_ICON_TEXT" > "$CONTROLLER_ICON_FILE"

# futási jog a menüopcióra
chmod u+x "$BRIDGE_ICON_FILE"
chmod u+x "$CONTROLLER_ICON_FILE"

# az asztal útvonalának kiderítése
: ${XDG_CONFIG_HOME:=~/.config}
[ -f "${XDG_CONFIG_HOME}/user-dirs.dirs" ] && . "${XDG_CONFIG_HOME}/user-dirs.dirs"
DESKTOP_DIR="${XDG_DESKTOP_DIR:-~/Desktop}"

# szimbólikus link az asztalra
ln -s "$BRIDGE_ICON_FILE" "$DESKTOP_DIR" > /dev/null 2>&1
ln -s "$CONTROLLER_ICON_FILE" "$DESKTOP_DIR" > /dev/null 2>&1

# ikonfájlok másolása
cp "$CURR_DIR/src/org/dyndns/fzoli/rccar/controller/resource/icon.png" "$ICON_PATH/$CONTROLLER_ICON_NAME"
cp "$CURR_DIR/src/org/dyndns/fzoli/rccar/bridge/resource/bridge.png" "$ICON_PATH/$BRIDGE_ICON_NAME"

# amíg bugos az swt taskbar, ez a fájl awt-re váltja azt
touch "$DIR_NAME/.noswt"

# telepítés vége üzenet
dialog --clear --backtitle "$TITLE" --msgbox "A telepítés végetért." 5 60
clear

