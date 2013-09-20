#!/bin/bash

set -e

. commons/http.sh

NOLOG=true

info_section "1/1 - Get All TT"
if [ -n "$1" ]; then
	get "api/troubleTicket?:fields=${1}"
else
	get "api/troubleTicket"
fi
