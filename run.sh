#!/bin/bash

mvn package

ARGS=""
for ARG in "$@"
do
	ARGS="$ARGS$ARG "
done

mvn exec:java -Dexec.mainClass="edu.virginia.aid.Driver" -Dexec.args="$ARGS"
