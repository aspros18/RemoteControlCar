#!/bin/bash

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
FILES=(one two three)

# ha kellenek a teszt tanúsítványok is, a könyvtár és a konfigok hozzáadása a listához
if [ $SAMPLE -eq 0 ] ; then
    FILES+=('four')
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

# fájlnév
FILE_NAME=${FILES[$COUNTER]}
# innen másol
SRC_FILE="$CURR_DIR/$FILE_NAME"
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
fi

# telepítés vége üzenet
dialog --clear --backtitle "$TITLE" --msgbox "A telepítés végetért." 5 60
clear

