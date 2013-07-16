#!/bin/sh

#
# Packaging script used for creating releases
#

# Step 1: Build the entire project

#mvn clean package shade:shade
mvn package

# Step 2: Copy over the dependencies

mvn dependency:copy-dependencies

# Step 3: Push everything into a target directory

mkdir target/lib


cp target/dependency/*.jar target/lib

cp core/target/*.jar target/lib
cp core/target/dependency/*.jar target/lib


cp parser/target/*.jar target/lib
cp parser/target/dependency/*.jar target/lib

cd installer

compile install.xml -b . -o ../target/pwe-installer.jar -k standard

