#!/bin/bash

set -e

usage() {
	nom=`basename $0`
	echo "+"
    echo "+ +  ${nom} [-n] Count TT"    
	echo "+ +  ${nom} [-c] Create list of TT with default file"
	echo "+ +  ${nom} [-c -f file ] Create list of TT with specified file"    
    echo "+ +  ${nom} [-d] Delete all TT"
    echo "+ +  ${nom} [-d -i id ] Delete single TT"    
	echo "+ +  ${nom} [-h] Help"  
    echo "+ +  query format: \"fields=x,y,...\"] attribute selection"
    echo "+ +  query format: \"key=value&...\"] attribute filtering"    
    echo "+ +  query format: \"fields=x,y,...&key=value&...\"] attribute selection and/or filtering"      
	echo "+"
	}

# HELP
if [ $# -eq 1 -a "$1" = -h ]; then usage; exit 2; fi

. commons/conf.sh
. commons/curl.sh

# OPTIONS
errOption=0
OPTIND=1
while getopts "cndf:i:q:" option
do
	case $option in
		c)  CREATE=OK
            ;;
		n)  COUNT=OK
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
        FILE=list-large.json
    fi
    post "api/admin/troubleTicket" $FILE
    exit 2
fi

# DELETE
if [ -n "$DELETE" ]; then
    if [ ! -n "$ID" ]; then
        echo "WARN: Delete all TT ? ctrl+c to break" >&2
        wait
    fi
    delete "api/admin/troubleTicket/${ID}"
    exit 2
fi

# COUNT
if [ -n "$COUNT" ]; then
    get "api/admin/troubleTicket/count"
    exit 2
fi

usage >&2


