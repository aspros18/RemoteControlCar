#!/bin/bash
# Az SWTJar felváltja ezt a scriptet, de Debian 6 (kernel: 3.0.0-1-amd64) alatt,
# GNOME felületen az ilyen módon betöltött program fagyást okoz az egyik gépemen
# a program (második) bezárásakor. Oka az, hogy az alkalmazás nem képes leállni.
# Meghagytam magamnak ezt a scriptet, hogy azon a gépen is jól működjön a program.
# Megjegyzés: ugyan azzal a géppel és rendszerrel, Xfce 4 felületen nincs ez a bug.
# UPDATE: amióta az SWTJar helyett külső jarból töltöm be az SWT-t, a bug megszünt.
cd `dirname $0`
# Az alkalmazást futtatja 64-bites Linux alatt.
java -splash:src/org/dyndns/fzoli/rccar/resource/splash.gif -classpath desktop/BrowserTest/lib/swt/swt-linux64-4.3M5a.jar:bin/ui.jar org.dyndns.fzoli.rccar.Main "$@"
