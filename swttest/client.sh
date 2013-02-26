#!/bin/bash
# A kliens alkalmazást futtatja.
cd `dirname $0`
java -jar ui.jar
./nikill.sh
