#!/bin/sh


# copy it over
cp ../dist/checklt-installer.jar ../../checklt-docs/releases/CheckLT

# change to that directory
cd ../../checklt-docs/releases

tar -cvf checklt-$1.tar CheckLT

gzip checklt-$1.tar


# build the zip
zip -r checklt-$1.zip CheckLT
