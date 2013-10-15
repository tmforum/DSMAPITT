#!/bin/bash

set -e

usage() {
	nom=`basename $0`
	echo "+"
    echo "+ +  ${nom} [-n] count tt"
    echo "+ +  ${nom} [-m] get mock tt"  
    echo "+ +  ${nom} [-x] invalid TT JPA cache"
    echo "+ +  ${nom} [-w long ] update delay with ms value between workflow steps" 
	echo "+ +  ${nom} [-h] help" 
	echo "+"
	}

# HELP
if [ $# -eq 1 -a "$1" = -h ]; then usage; exit 2; fi

. commons/conf.sh
. commons/curl.sh

# OPTIONS
errOption=0
OPTIND=1
while getopts "mndxi:q:w:c:" option
do
	case $option in
		m)  MOCK=OK
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
        w)  DELAY="${OPTARG}"
			;;            
		\?) echo " option $OPTARG INVALIDE" >&2
			errOption=3
	esac
done

if [ $errOption == 3 ]; then usage >&2; exit $errOption; fi

# COUNT
if [ -n "$COUNT" ]; then
    get "api/admin/tt/count"
    exit 2
fi

# MOCK
if [ -n "$MOCK" ]; then
    get "api/admin/tt/mock"
    exit 2
fi

# CACHE
if [ -n "$CACHE" ]; then
    delete "api/admin/tt/cache"
    exit 2
fi

# DELAY
if [ -n "$DELAY" ]; then
    put "api/admin/tt/wf/delay/${DELAY}"
    exit 2
fi

usage >&2


