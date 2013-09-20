#!/bin/bash

set -e

. commons/http.sh

NOLOG=true

info_section "1/1 - Put 1 TT"
put "api/troubleTicket/${1}" put-tt.json
