#!/bin/bash

USER=admin
PASSWORD=

# xml or json...
FORMAT=json

PROTOCOL=http
HOST=localhost
PORT=8080
CONTEXT=DSMAPITT

CURL_OPTS="-v --insecure --user ${USER}:${PASSWORD} --header Content-Type:application/json --header Accept:application/${FORMAT}"
URL_BASE="${PROTOCOL}://${HOST}:${PORT}/${CONTEXT}"

outfile=$(mktemp)
errfile=$(mktemp)

log() {
    [ -n "$NOLOG" ] || printf "[%s:%06.3f] %s\n" $(date +'%FT%H:%M %S.%N') "$1"
}

get() {
    if [ -z "$(echo "$1" | grep "$HOST")" ]; then
        log "curl [...] ${URL_BASE}/${1}"
        curl ${CURL_OPTS} ${URL_BASE}/${1} >$outfile 2>$errfile
    else
        log "curl [...] ${1}"
        curl ${CURL_OPTS} ${1} >$outfile 2>$errfile
    fi
}

post() {
    log "curl [...] -X POST ${URL_BASE}/${1}"
    curl ${CURL_OPTS} -X POST -d @${2} ${URL_BASE}/${1} >$outfile 2>$errfile
}

delete() {
    if [ -z "$(echo "$1" | grep "$HOST")" ]; then
        log "curl [...] -X DELETE ${URL_BASE}/${1}"
        curl ${CURL_OPTS} -X DELETE ${URL_BASE}/${1} >$outfile 2>$errfile
    else
        log "curl [...] -X DELETE ${1}"
        curl ${CURL_OPTS} -X DELETE ${1} >$outfile 2>$errfile
    fi
}

put() {
    if [ -z "$(echo "$1" | grep "$HOST")" ]; then
        log "curl [...] -X PUT ${URL_BASE}/${1}"
        curl ${CURL_OPTS} -X PUT -d @${2} ${URL_BASE}/${1} >$outfile 2>$errfile
    else
        log "curl [...] -X PUT ${1}"
        curl ${CURL_OPTS} -X PUT -d @${2} ${1} >$outfile 2>$errfile
    fi
}

wait() {
    read -s
}

section() {
    printf "\n>>> %s <<<\n\n" "$1"
    wait
}
