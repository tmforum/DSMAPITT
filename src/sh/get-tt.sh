#!/bin/bash

set -e

. commons/http.sh

NOLOG=true

info_section "1/1 - Get TT by id"
if [ -n "$2" ];then
	get "api/troubleTicket/${1}?:fields=${2}"
else
	get "api/troubleTicket/${1}"
fi
