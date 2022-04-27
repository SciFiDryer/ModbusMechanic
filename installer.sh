#!/bin/bash

DIR=$(dirname "${BASH_SOURCE[0]}") # Relative
DIR=$(realpath "${DIR}")          # Absolute
cd $DIR

DIR_DESK=~/.local/share/applications
# DIR_DESK=$DIR
APP_NAME=modbusmechanic.desktop
if [[ -f "$DIR_DESK/$APP_NAME" ]]; then
    echo Link "$DIR_DESK/$APP_NAME exists."
    rm $DIR_DESK/$APP_NAME
else
    echo Link "$DIR_DESK/$APP_NAME does not exist."
fi

if [[ -f "$DIR/$APP_NAME" ]]; then
    echo Launcher "$DIR/$APP_NAME exists."
    rm $DIR/$APP_NAME
else
    echo Launcher "$DIR/$APP_NAME does not exist."
fi

echo \
"[Desktop Entry]
Encoding=UTF-8
Type=Application
NoDisplay=false
Terminal=false
Icon=
Comment=
Categories=
Exec=$DIR/modbusmechanic.sh
Name=Modbus Mechanic v2.1" \
>> $APP_NAME

ln -s $DIR/$APP_NAME $DIR_DESK/$APP_NAME
