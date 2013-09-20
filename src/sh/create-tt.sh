#!/bin/bash

set -e

. commons/http.sh

NOLOG=true

info_section "1/1 - Create 1 TT"
post "api/troubleTicket" create-tt-1.json
