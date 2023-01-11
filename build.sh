#!/bin/bash
mvn clean
mvn package spring-boot:repackage
cp target/via-w2w.jar .