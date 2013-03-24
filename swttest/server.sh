#!/bin/bash
# A szerver alkalmazást futtatja.
cd `dirname $0`
java -jar ui.jar server "$@"
#./nikill.sh
