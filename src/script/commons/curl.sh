#!/bin/bash

JSON_HOME="json/"

# xml or json...
FORMAT=json

PROTOCOL=http

mkdir -p $LOG_HOME

outfile="${LOG_HOME}/${CONTEXT}.out"
logfile="${LOG_HOME}/${CONTEXT}.log"
tmpfile=`mktemp -p ${LOG_HOME} ${CONTEXT}.out.XXXXXX`

CURL_OPTS="-v --write-out @commons/curl_template --insecure --user ${USER}:${PASSWORD} --header Content-Type:application/json --header Accept:application/${FORMAT}"
URL_BASE="${PROTOCOL}://${HOST}:${PORT}/${CONTEXT}"

get() {
    info_response
    curl ${CURL_OPTS} ${URL_BASE}/${1} >>$tmpfile 2>$logfile
    info_final "GET"
}

post() {
    info_response
    curl ${CURL_OPTS} -X POST -d @${JSON_HOME}${2} ${URL_BASE}/${1} >>$tmpfile 2>$logfile
    info_final "POST" $2
}

delete() {
    info_response      
    curl ${CURL_OPTS} -X DELETE ${URL_BASE}/${1} >>$tmpfile 2>$logfile
    info_final "DELETE"
}

put() {
    info_response       
    curl ${CURL_OPTS} -X PUT -d @${JSON_HOME}${2} ${URL_BASE}/${1} >>$tmpfile 2>$logfile
    info_final "PUT"
}

patch() {
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
	printf "   METHOD:${1}\n" >>$tmpfile 2>>$logfile
    if [ -n "$FILE" ]; then
        printf "   INPUT FILE:$JSON_HOME$FILE\n" >>$tmpfile 2>>$logfile
    fi
    printf "\n" >>$tmpfile 2>>$logfile        
	cat $tmpfile >> $outfile
	cat $tmpfile
    rm -f $tmpfile
}
