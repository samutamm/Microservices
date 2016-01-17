#!/bin/bash
echo "Person service!"
export CONF_API="http://localhost:4569/configurations"
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
NEWPATH=$NEWPATH"/PersonService/"
POMPATH=$NEWPATH"pom.xml"
echo $NEWPATH

mvn clean install exec:java -Dexec.mainClass=com.mycompany.personservice.Main -f $POMPATH