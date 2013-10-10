#!/bin/bash

set -e

usage() {
	nom=`basename $0`
	echo "+"
	echo "+ +  ${nom} [-c] Create Hub with default file"
	echo "+ +  ${nom} [-c -f file ] Create Hub with specified file"
	echo "+ +  ${nom} [-p] Patch Hub with default file"
	echo "+ +  ${nom} [-p -f file ] Patch Hub with specified file"
    echo "+ +  ${nom} [-d] Delete all Hub"    
    echo "+ +  ${nom} [-d -i id ] Delete single Hub" 
    echo "+ +  ${nom} [-l] List all Hub"
    echo "+ +  ${nom} [-g -i id] Retrieve single Hub"
	echo "+ +  ${nom} [-h] Help"   
	echo "+"
	}

# HELP
if [ $# -eq 1 -a "$1" = -h ]; then usage; exit 2; fi

. commons/conf.sh
. commons/curl.sh

# OPTIONS
errOption=0
OPTIND=1
while getopts "cupgldf:i:q:" option
do
	case $option in
		c)  CREATE=OK
            ;;
        p)  PATCH=OK
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
    if [ ! -n "$FILE" ]; then
        echo "Please provide [-f file]" >&2
        exit 4 
    fi
    post "api/hub" $FILE
    exit 2
fi

# PATCH
if [ -n "$PATCH" ]; then
    if [ ! -n "$ID" ]; then
        echo "Please provide [-i id]" >&2
        exit 4
    fi
    if [ ! -n "$FILE" ]; then
        echo "Please provide [-f file]" >&2
        exit 4 
    fi
    patch "api/hub/${ID}" $FILE
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


