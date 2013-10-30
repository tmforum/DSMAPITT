#!/bin/bash

set -e

RESOURCE=troubleTicket

usage() {
	nom=`basename $0`
	echo "+"
    echo "+ +  ${nom} [-m] get mock"    
	echo "+ +  ${nom} [-c file ] post with specified file"
	echo "+ +  ${nom} [-p file ] patch with specified file"
	echo "+ +  ${nom} [-u file ] put with specified file"
    echo "+ +  ${nom} [-g] list all"
    echo "+ +  ${nom} [-g -q \"query\"] list all with attribute selection and/or attribute filtering"
    echo "+ +  ${nom} [-g -i id] get single"
    echo "+ +  ${nom} [-g -i id -q \"query\"] get single with attribute selection"
	echo "+ +  ${nom} [-h] help"   
    echo "+ +  query format: \"fields=x,y,...\"] attribute selection"
    echo "+ +  query format: \"key=value&...\"] attribute filtering"    
    echo "+ +  query format: \"fields=x,y,...&key=value&...\"] attribute selection and/or filtering"  
	echo "+"
	}

# HELP
if [ $# -eq 1 -a "$1" = -h ]; then usage; exit 2; fi

# OPTIONS
errOption=0
OPTIND=1
while getopts "mgi:q:c:u:p:" option
do
	case $option in
		m)  MOCK=OK
            ;;     
		c)  POST=OK
            FILE="${OPTARG}"        
            ;;           
        u)  PUT=OK
            FILE="${OPTARG}"          
            ;;
        p)  PATCH=OK
            FILE="${OPTARG}"          
            ;;
        g)  GET=OK
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

. commons/conf.sh
. commons/curl.sh

usage >&2


