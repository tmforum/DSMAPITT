#!/bin/bash

set -e

usage() {
	nom=`basename $0`
	echo "+"
	echo "+ +  ${nom} [-c file ] post with specified file"
	echo "+ +  ${nom} [-p file ] patch with specified file"   
    echo "+ +  ${nom} [-d -i id ] delete single" 
    echo "+ +  ${nom} [-l] list all"
    echo "+ +  ${nom} [-g -i id] get single"
    echo "+ +  ${nom} [-d] admin only - delete all"     
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
while getopts "ugldi:q:c:p:" option
do
	case $option in
		c)  CREATE=OK
            FILE="${OPTARG}"        
            ;;
        p)  PATCH=OK
            FILE="${OPTARG}"         
            ;;
        l)  GET=OK
			;;
        g)  GET=OK
			;;
        d)  DELETE=OK
            ;; 
        i)  ID="${OPTARG}"
            ;;
        f)  FILE="${OPTARG}"
			;;
		\?) echo " option $OPTARG INVALIDE" >&2
			errOption=3
	esac
done

if [ $errOption == 3 ]; then usage >&2; exit $errOption; fi

# CREATE
if [ -n "$CREATE" ]; then
    post "api/hub"
    exit 2
fi

# PATCH
if [ -n "$PATCH" ]; then
    if [ ! -n "$ID" ]; then
        echo "Please provide [-i id]" >&2
        exit 4
    fi
    patch "api/hub/${ID}"
    exit 2
fi

# GET
if [ -n "$GET" ]; then
    get "api/hub/${ID}"
    exit 2
fi

# DELETE
if [ -n "$DELETE" ]; then
    if [ ! -n "$ID" ]; then
        echo "WARN: Delete all Hub ? ctrl+c to break" >&2
        wait
        delete "api/admin/hub"
    else
        delete "api/hub/${ID}"
    fi
    exit 2
fi

usage >&2


