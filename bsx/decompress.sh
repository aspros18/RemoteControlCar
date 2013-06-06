#!/bin/bash
export TMP_DIR=`mktemp -d /tmp/mobilerc-installer.XXXXXX`
ARCHIVE=`awk '/^__ARCHIVE_BELOW__/ {print NR + 1; exit 0; }' $0`
tail -n+$ARCHIVE $0 | tar xzv -C $TMP_DIR > /dev/null 2>&1
CDIR=`pwd`
cd $TMP_DIR
bash linux-installer.sh
cd $CDIR
rm -rf $TMP_DIR
exit 0
__ARCHIVE_BELOW__
