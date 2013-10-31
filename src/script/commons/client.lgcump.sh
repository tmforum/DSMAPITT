#!/bin/bash

usage() {
    nom=`basename $0`
    echo "+"
    echo "+ +  ${nom} [-l] list all elements"
    echo "+ +  ${nom} [-l -q \"query\"] list all elements with attribute selection and/or attribute filtering"    
    echo "+ +  ${nom} [-g id] get single element"
    echo "+ +  ${nom} [-g id -q \"query\"] get single element with attribute selection"    
    echo "+ +  ${nom} [-c file ] post with specified file"
    echo "+ +  ${nom} [-u file ] put with specified file"
    echo "+ +  ${nom} [-p file ] patch with specified file"    
    echo "+ +  ${nom} [-m] get mock resource"        
    echo "+ +  ${nom} [-h] Help"   
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
while getopts "lg:c:u:mp:q:" option
do
	case $option in
        l)  GET=OK   
			;;
        g)  GET=OK
            ID="${OPTARG}"        
			;;
		c)  POST=OK
            FILE="${OPTARG}"
            ;;
        u)  PUT=OK
            FILE="${OPTARG}"        
            ;;
        m)  MOCK=OK
			;;
        p)  PATCH=OK
            FILE="${OPTARG}"        
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


