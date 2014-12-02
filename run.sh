#!/bin/bash

printf "\033[1mCompile\n\033[0m"
mvn compile

ARGS=""
for ARG in "$@"
do
	ARGS="$ARGS$ARG "
done

printf "\x1b[1m\nRun\n\x1b[0m"
mvn exec:java -q -Dexec.mainClass="edu.virginia.aid.util.Driver" -Dexec.args="$ARGS"
