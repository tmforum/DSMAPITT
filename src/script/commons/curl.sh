#!/bin/bash

# xml or json...
FORMAT=json

PROTOCOL=http

mkdir -p $LOG_HOME

outfile="${LOG_HOME}/${CONTEXT}.out"
logfile="${LOG_HOME}/${CONTEXT}.log"
tmpfile=`mktemp -p ${LOG_HOME} ${CONTEXT}.out.XXXXXX`

CURL_OPTS="--write-out @commons/curl_template --insecure --user ${USER}:${PASSWORD} --header Content-Type:application/json --header Accept:application/${FORMAT}"
URL_BASE="${PROTOCOL}://${HOST}:${PORT}/${CONTEXT}"

mycurl() {    
    CURL_ARGS="${CURL_OPTS} -X ${1} ${URL_BASE}/${2}"   # 1 method - 2 relative url - 3 file
    if [ -n "$FILE" ]; then    # If file for post/put/patch
        CURL_ARGS="${CURL_ARGS} -d @${FILE}"
    fi
    info_request "${1}" "${URL_BASE}/${2}" "${3}"
    info_response
    curl $CURL_ARGS >>$tmpfile 2>$logfile
    info_command "$CURL_ARGS"    
    info_final    
}

put() {
    mycurl "PUT" $1
}

patch() {
    mycurl "PATCH" $1    
}

post() {
    mycurl "POST" $1    
}

delete() {
    mycurl "DELETE" $1    
}

get() {
    mycurl "GET" $1    
}

wait() {
    read -s
}

info_command() {
	printf "\n   COMMAND" >>$tmpfile    
    printf "\n   curl ${1}\n\n" >>$tmpfile
}

info_request() {
    printf "   REQUEST ${1} ${2}\n" >>$tmpfile       
    if [ -n "$FILE" ]; then    # If file for post/put/patch
        printf "\n   REQUEST BODY:\n" >>$tmpfile
        cat $FILE >> $tmpfile
    fi
}

info_response() {
	printf "\n   RESPONSE\n" >>$tmpfile
}

info_final() {
    printf "\n" >>$tmpfile 2>>$logfile        
	cat $tmpfile >> $outfile
	cat $tmpfile
    rm -f $tmpfile
}

