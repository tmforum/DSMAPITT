#!/bin/bash

set -e

usage() {
	nom=`basename $0`
	echo "+"
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

# DELETE
if [ -n "$DELETE" ]; then
        echo "WARN: Delete all event ? ctrl+c to break" >&2
        wait
        delete "api/admin/event"
    exit 2
fi

usage >&2


