#!/usr/bin/env bash

for mib in ./extra-smi-mibs/*
do
    java -classpath "./bin" MibConverter $mib
done
