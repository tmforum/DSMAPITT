#!/bin/bash

JSON_HOME="../json/"

USER=admin
PASSWORD=admin

# xml or json...
FORMAT=json

PROTOCOL=http
HOST=localhost
PORT=8080
CONTEXT=DSMAPITT

mkdir -p tmp

outfile="tmp/${CONTEXT}.out"
logfile="tmp/${CONTEXT}.log"

tmpfile=`mktemp -p tmp ${CONTEXT}.out.XXXXXX`

CURL_OPTS="-v --write-out @commons/curl_template --insecure --user ${USER}:${PASSWORD} --header Content-Type:application/json --header Accept:application/${FORMAT}"
URL_BASE="${PROTOCOL}://${HOST}:${PORT}/${CONTEXT}"

log() {
    [ -n "$NOLOG" ] || printf "[%s:%06.3f] %s\n" $(date +'%FT%H:%M %S.%N') "$1"
}

get() {
    log "curl [...] ${1}"
    info_response
    curl ${CURL_OPTS} ${URL_BASE}/${1} >>$tmpfile 2>$logfile
    info_final "GET"
}

post() {
    log "curl [...] -X POST -d @${JSON_HOME}${2} ${URL_BASE}/${1}"
    info_response
    curl ${CURL_OPTS} -X POST -d @${JSON_HOME}${2} ${URL_BASE}/${1} >>$tmpfile 2>$logfile
    info_final "POST"
}

delete() {
    log "curl [...] -X DELETE ${1}"
    info_response      
    curl ${CURL_OPTS} -X DELETE ${URL_BASE}/${1} >>$tmpfile 2>$logfile
    info_final "DELETE"
}

put() {
    log "curl [...] -X PUT ${1}"
    info_response       
    curl ${CURL_OPTS} -X PUT -d @${JSON_HOME}${2} ${URL_BASE}/${1} >>$tmpfile 2>$logfile
    info_final "PUT"
}

patch() {
    log "curl [...] -X PATCH ${1}"
    info_response        
    curl ${CURL_OPTS} -X PATCH -d @${JSON_HOME}${2} ${URL_BASE}/${1} >>$tmpfile 2>$logfile
    info_final "PATCH"
}

wait() {
    read -s
}

info_section() {
    printf "\n   STEP:%s\n" "$1" >$tmpfile
}

info_response() {
	printf "\n   RESPONSE:\n" >>$tmpfile
}

info_final() {
	printf "   METHOD:${1}\n\n" >>$tmpfile 2>>$logfile
	cat $tmpfile >> $outfile
	cat $tmpfile
}
