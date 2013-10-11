#!/bin/bash

set -e

usage() {
	nom=`basename $0`
	echo "+"
    echo "+ +  ${nom} [-n] Count TT"
    echo "+ +  ${nom} [-m] Get Mock"  
	echo "+ +  ${nom} [-c] Create list of TT with default file"
	echo "+ +  ${nom} [-c -f file ] Create list of TT with specified file"    
    echo "+ +  ${nom} [-d] Delete all TT"
    echo "+ +  ${nom} [-d -i id ] Delete single TT"
    echo "+ +  ${nom} [-x] Invalid TT JPA cache"   
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
while getopts "mcndf:i:q:" option
do
	case $option in
		m)  MOCK=OK
            ;;    
		c)  CREATE=OK
            ;;
		n)  COUNT=OK
            ;;
		n)  CACHE=OK
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
    mycurl "POST" "api/admin/troubleTicket"
    exit 2
fi

# DELETE
if [ -n "$DELETE" ]; then
    if [ ! -n "$ID" ]; then
        echo "WARN: Delete all TT ? ctrl+c to break" >&2
        wait
    fi
    mycurl "DELETE" "api/admin/troubleTicket/${ID}"
    exit 2
fi

# COUNT
if [ -n "$COUNT" ]; then
    mycurl "GET" "api/admin/troubleTicket/count"
    exit 2
fi

# MOCK
if [ -n "$MOCK" ]; then
    mycurl "GET" "api/admin/troubleTicket/mock"
    exit 2
fi

# CACHE
if [ -n "$CACHE" ]; then
    mycurl "DELETE" "api/admin/troubleTicket/cache"
    exit 2
fi

usage >&2


