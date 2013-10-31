#!/bin/bash

set -e

RESOURCE=admin/troubleTicket

usage() {
	nom=`basename $0`
	echo "+"
    echo "+ +  ${nom} [-n] count"
    echo "+ +  ${nom} [-d] delete all"
    echo "+ +  ${nom} [-d id ] delete single"
    echo "+ +  ${nom} [-c file ] post list with specified file"
    echo "+ +  ${nom} [-x] invalid JPA cache"
    echo "+ +  ${nom} [-w long ] update wait time with ms value between workflow steps" 
	echo "+ +  ${nom} [-h] help" 
	echo "+"
	}

# HELP
if [ $# -eq 1 -a "$1" = -h ]; then usage; exit 2; fi

# OPTIONS
errOption=0
OPTIND=1
while getopts "ndc:xw:" option
do
	case $option in
        d)  DELETE=OK
            ;;        
		c)  POST=OK
            FILE="${OPTARG}"
            ;;
		n)  COUNT=OK
            ;;
		x)  CACHE=OK
            ;;            
        i)  ID="${OPTARG}"
            ;;
        w)  WAIT="${OPTARG}"
			;;            
		\?) echo " option $OPTARG INVALIDE" >&2
			errOption=3
	esac
done

if [ $errOption == 3 ]; then usage >&2; exit $errOption; fi

. commons/conf.sh
. commons/curl.sh

# MOCK
if [ -n "$MOCK" ]; then
    get "mock"
    exit 2
fi

# CACHE
if [ -n "$CACHE" ]; then
    delete "cache"
    exit 2
fi

# DELAY
if [ -n "$WAIT" ]; then
    put "wf/delay/${WAIT}"
    exit 2
fi

usage >&2


