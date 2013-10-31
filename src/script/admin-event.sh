#!/bin/bash

set -e

RESOURCE=admin/event

usage() {
	nom=`basename $0`
	echo "+"
    echo "+ +  ${nom} [-d] delete all"    
	echo "+ +  ${nom} [-h] help"   
	echo "+"
	}

# HELP
if [ $# -eq 1 -a "$1" = -h ]; then usage; exit 2; fi

# OPTIONS
errOption=0
OPTIND=1
while getopts "d" option
do
	case $option in
        d)  DELETE=OK
            ;; 
		\?) echo " option $OPTARG INVALIDE" >&2
			errOption=3
	esac
done

if [ $errOption == 3 ]; then usage >&2; exit $errOption; fi

. commons/conf.sh
. commons/curl.sh

usage >&2


