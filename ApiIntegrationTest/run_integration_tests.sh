#!/bin/bash
echo "Integration project"
export CONF_API="http://localhost:4569/configurations"
mvn clean install exec:java -Dexec.mainClass=com.mycompany.apiintegrationtest.Main