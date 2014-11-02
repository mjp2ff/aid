#!/bin/bash

# Get WordNet dictionary
WORDNET_DIR=./wordnet/dict

if [ ! -d $WORDNET_DIR ]; then
	mkdir -p wordnet/
    cd wordnet && curl -o dict.tar.gz http://wordnetcode.princeton.edu/wn3.1.dict.tar.gz
    tar -xzvf dict.tar.gz
    rm dict.tar.gz
fi

# Set up benchmark script
sed "s#^\([^#]\)#$PWD\/\1#g" benchmark/benchmark.csv > benchmark/benchmark-runnable.csv

