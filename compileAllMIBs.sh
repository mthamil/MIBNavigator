#!/usr/bin/env bash

for mib in ./extra-smi-mibs/*
do
	# Create the output path.
	mibBaseName=$(basename "$mib")
	mibBaseName=${mibBaseName%.*}
	convertedFilePath="./extra-xml-mibs/${mibBaseName}.xml"
	#echo $convertedFilePath
	
    java -classpath "./bin" MIBConverter $mib $convertedFilePath
done
