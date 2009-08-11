#!/usr/bin/env bash

for mib in ./mibs/smi/*.mib
do
    java -classpath "./bin" MIBConverter $mib ./mibs/xml/
done

for mib in ./mibs/smi/*.MIB
do
    java -classpath "./bin" MIBConverter $mib ./mibs/xml/
done
