#!/bin/bash

printf "\033[1mCompiling: \033[0m"
mvn compile -q
printf "Done\n"

ARGS=""
for ARG in "$@"
do
	ARGS="$ARGS$ARG "
done

printf "\x1b[1mRunning:\n\x1b[0m"
mvn exec:java -q -Dexec.mainClass="edu.virginia.aid.Driver" -Dexec.args="$ARGS"
