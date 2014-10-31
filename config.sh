#!/bin/bash

WORDNET_DIR=./wordnet/dict

if [ ! -d $WORDNET_DIR ]; then
	mkdir -p wordnet/
    cd wordnet && curl -o dict.tar.gz http://wordnetcode.princeton.edu/wn3.1.dict.tar.gz
    tar -xzvf dict.tar.gz
    rm dict.tar.gz
fi
