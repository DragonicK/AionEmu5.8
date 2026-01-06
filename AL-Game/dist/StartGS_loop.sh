#!/bin/bash

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
LIB_DIR="${SCRIPT_DIR}/libs"
if [ ! -d "$LIB_DIR" ]; then
  LIB_DIR="${SCRIPT_DIR}/../libs"
fi
JAR_PATH="${SCRIPT_DIR}/AL-Game.jar"
if [ ! -f "$JAR_PATH" ]; then
  JAR_PATH="${SCRIPT_DIR}/../AL-Game.jar"
fi

err=1
until [ "$err" = 0 ];
do
	[ -d log/ ] || mkdir log/
	[ -d log/backup ] || mkdir -p log/backup
	[ -f log/console.log ] && mv log/console.log "log/backup/`date +%Y-%m-%d_%H-%M-%S`_console.log"
	java -Xms4096m -Xmx8192m -ea -javaagent:"${LIB_DIR}/al-commons.jar" -cp "${LIB_DIR}/*:${JAR_PATH}" com.aionemu.gameserver.GameServer > log/console.log 2>&1
	err=$?
	gspid=$!
	echo ${gspid} > gameserver.pid
	sleep 10
done
