#!/bin/bash
cd `dirname $0`
java -classpath ui.jar org.dyndns.fzoli.rccar.bridge.Main "$@"
