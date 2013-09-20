#!/bin/bash

set -e

. commons/http.sh

NOLOG=true

info_section "1/1 - Patch 1 TT"
patch "api/troubleTicket/${1}" patch-tt.json
