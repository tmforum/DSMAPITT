#!/bin/bash

set -e

RESOURCE=hub

usage() {
	nom=`basename $0`
	echo "+"
    echo "+ +  ${nom} [-l] list all"
    echo "+ +  ${nom} [-g id] get single"      
	echo "+ +  ${nom} [-c file ] post with specified file"
	echo "+ +  ${nom} [-p file ] patch with specified file"   
    echo "+ +  ${nom} [-d id ] delete single"   
	echo "+ +  ${nom} [-h] help"   
	echo "+"
	}

# HELP
if [ $# -eq 1 -a "$1" = -h ]; then usage; exit 2; fi

# OPTIONS
errOption=0
OPTIND=1
while getopts "lg:c:p:d:" option
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
        p)  PATCH=OK
            FILE="${OPTARG}"        
            ;;
        d)  DELETE=OK
            ID="${OPTARG}"
			;;       
		\?) echo " option $OPTARG INVALIDE" >&2
			errOption=3
	esac
done

if [ $errOption == 3 ]; then usage >&2; exit $errOption; fi

. commons/conf.sh
. commons/curl.sh

usage >&2


