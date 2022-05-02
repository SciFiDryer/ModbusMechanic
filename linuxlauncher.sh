#!/bin/bash
DIR=$(dirname "${BASH_SOURCE[0]}") # Relative
DIR=$(realpath "${DIR}")          # Absolute
APP_NAME=modbusmechanic.desktop
cd $DIR
DIR_DESK=~/.local/share/applications

function run_disown() {
	"$@" & disown
}

function run_disown_silence(){
	run_disown "$@" 1>/dev/null 2>/dev/null
}


if [ "$1" = "--install" ] ; then
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
		Exec="$DIR/linuxlauncher.sh --launch"
		Name=ModbusMechanic" \
		>> $APP_NAME

	ln -s $DIR/$APP_NAME $DIR_DESK/$APP_NAME
	chmod +x $DIR/linuxlauncher.sh
elif [ "$1" = "--uninstall" ] ; then
	rm -f $DIR/$APP_NAME $DIR_DESK/$APP_NAME
elif [ "$1" = "--launch" ] ; then
	DIR=$(dirname "${BASH_SOURCE[0]}") # Relative

	cd $DIR
	run_disown_silence java -jar ModbusMechanic.jar
else
	echo "ModbusMechanic Linux launcher
Linux launcher related script

Options:
  --install 		Installs the launcher icon
  --uninstall		Removes the launcher icon
  --launch		Invoked by the launcher icon to start ModbusMechanic"
fi
