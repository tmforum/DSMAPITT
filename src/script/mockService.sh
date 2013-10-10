#!/bin/bash

set -e

nom=`basename $0`
echo "+"
echo "+ +  ${nom} port pathToLogFile"
echo "+ +"      
echo "+ +  ...listening at http://localhost:$1/listen"
echo "+ +"       
echo "+ +  ...logging to $2"
echo "+"

java -jar lib/DSMockService-1.0.jar $1 $2
