@echo off
start "Bridge server" /B "javaw" -cp .;ui.jar org.dyndns.fzoli.rccar.bridge.Main %*
