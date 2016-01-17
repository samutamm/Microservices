#!/bin/bash
echo "Configuration service!"
export PORT=4569
export ENCRYPTION_KEY="foobar"
CURRENTPATH=${PWD}
arr=$(echo $CURRENTPATH | tr "/" "\n")
NEWPATH=""
for x in $arr
do
    NEWPATH=$NEWPATH"/"$x
    if [ $x = "Microservices" ] ; then
        break
    fi
done
NEWPATH=$NEWPATH"/ConfigurationService/"
POMPATH=$NEWPATH"pom.xml"
echo $NEWPATH

mvn clean install exec:java -Dexec.mainClass=com.mycompany.configurationservice.Main -f $POMPATH