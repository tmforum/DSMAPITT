#!/bin/bash

set -e

nom=`basename $0`
echo "+"
echo "+ +  ${nom} port pathToLogFile"
echo "+ +"      
echo "+ +  ...listening at http://localhost:$1/listener"
echo "+ +"       
echo "+ +  ...logging to $2"
echo "+"

printf "listening at http://localhost:$1/listener\n\n" >>$2

java -jar lib/DSMockService-1.0.jar $1 $2
