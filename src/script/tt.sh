#!/bin/bash

set -e

usage() {
	nom=`basename $0`
	echo "+"
	echo "+ +  ${nom} [-c file ] post with specified file"
	echo "+ +  ${nom} [-p file ] patch with specified file"
	echo "+ +  ${nom} [-u file ] put with specified file"
    echo "+ +  ${nom} [-l] list all"
    echo "+ +  ${nom} [-l -q \"query\"] list all with attribute selection and/or attribute filtering"
    echo "+ +  ${nom} [-g -i id] get single"
    echo "+ +  ${nom} [-g -i id -q \"query\"] get single with attribute selection"
    echo "+ +  ${nom} [-d] admin only - delete all"
    echo "+ +  ${nom} [-d -i id ] admin only - delete single"
    echo "+ +  ${nom} [-a file ] admin only - post list with specified file"
	echo "+ +  ${nom} [-h] help"   
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
while getopts "dgli:q:c:u:p:a:" option
do
	case $option in
		c)  POST=OK
            FILE="${OPTARG}"        
            ;;
		a)  POST_MULTI=OK
            FILE="${OPTARG}"        
            ;;            
        u)  PUT=OK
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
        q)  QUERY="${OPTARG}"
			;;
		\?) echo " option $OPTARG INVALIDE" >&2
			errOption=3
	esac
done

if [ $errOption == 3 ]; then usage >&2; exit $errOption; fi

# POST
if [ -n "$POST" ]; then
    post "api/troubleTicket"
    exit 2
fi

# PUT
if [ -n "$PUT" ]; then
    if [ ! -n "$ID" ]; then
        echo "Please provide [-i id]" >&2
        exit 4
    fi    
    put "api/troubleTicket/${ID}"
    exit 2
fi

# PATCH
if [ -n "$PATCH" ]; then
    if [ ! -n "$ID" ]; then
        echo "Please provide [-i id]" >&2
        exit 4
    fi
    patch "api/troubleTicket/${ID}"
    exit 2
fi

# GET
if [ -n "$GET" ]; then
    if [ -n "$QUERY" ]; then
        QUERY="?$QUERY"
    fi
    get "api/troubleTicket/${ID}${QUERY}"
    exit 2
fi

# ADMIN ONLY STUFF

# DELETE
if [ -n "$DELETE" ]; then
    if [ ! -n "$ID" ]; then
        echo "WARN: Delete all TT ? ctrl+c to break" >&2
        wait
    fi
    delete "api/admin/tt/${ID}"
    exit 2
fi

# POST
if [ -n "$POST_MULTI" ]; then
    post "api/admin/tt"
    exit 2
fi

usage >&2


