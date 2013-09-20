#!/bin/bash

set -e

. commons/http.sh

NOLOG=true

info_section "1/1 - Create 10 TT"
post "api/admin/troubleTicket" create-tt-10.json
