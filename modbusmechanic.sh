#!/bin/bash

DIR=$(dirname "${BASH_SOURCE[0]}") # Relative

function run_disown() {
    "$@" & disown
}

function run_disown_silence(){
    run_disown "$@" 1>/dev/null 2>/dev/null
}

run_disown_silence java -jar $DIR/ModbusMechanic.jar
