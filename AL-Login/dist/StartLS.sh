#!/bin/sh

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
LIB_DIR="${SCRIPT_DIR}/libs"
if [ ! -d "$LIB_DIR" ]; then
	LIB_DIR="${SCRIPT_DIR}/../libs"
fi
JAR_PATH="${SCRIPT_DIR}/AL-Login.jar"
if [ ! -f "$JAR_PATH" ]; then
	JAR_PATH="${SCRIPT_DIR}/../AL-Login.jar"
fi

err=1
until [ "$err" = 0 ];
do
	java -Xms8m -Xmx32m -ea -cp "$LIB_DIR/*:$JAR_PATH" com.aionemu.loginserver.LoginServer
	err=$?
	lspid=$!
	echo ${lspid} > loginserver.pid
	echo "LoginServer started!"
	sleep 10
done
