#!/bin/bash

PROTOCOL=http

mkdir -p $LOG_HOME

outfile="${LOG_HOME}/out"
logfile="${LOG_HOME}/log"
tmpfile=`mktemp -p ${LOG_HOME} .out.XXXXXX`

CURL_OPTS="--write-out @commons/curl_template --insecure --user ${USER}:${PASSWORD} --header Content-Type:application/json --header Accept:application/json"

URL=$URL_BASE/$RESOURCE

mycurl() {    
    CURL_ARGS="${CURL_OPTS} -X ${1} ${URL}/${2}"   # 1 method - 2 relative url - 3 file
    if [ -n "$FILE" ]; then    # If file for post/put/patch
        CURL_ARGS="${CURL_ARGS} -d @${FILE}"
    fi
    info_request "${1}" "${URL}/${2}" "${3}"
    info_response
    curl $CURL_ARGS >>$tmpfile 2>$logfile
    info_command "$CURL_ARGS"    
    info_final    
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

# DEFAULT USAGES

# POST
if [ -n "$POST" ]; then
    mycurl "POST"
    exit 2
fi

# PUT
if [ -n "$PUT" ]; then
    if [ ! -n "$ID" ]; then
        echo "Please provide [-i id]" >&2
        exit 4
    fi    
    put "${ID}"
    exit 2
fi

# PATCH
if [ -n "$PATCH" ]; then
    if [ ! -n "$ID" ]; then
        echo "Please provide [-i id]" >&2
        exit 4
    fi
    patch "${ID}"
    exit 2
fi

# GET
if [ -n "$GET" ]; then
    if [ -n "$QUERY" ]; then
        QUERY="?$QUERY"
    fi
    get "${ID}${QUERY}"
    exit 2
fi

# DELETE
if [ -n "$DELETE" ]; then
    if [ ! -n "$ID" ]; then
        echo "WARN: Delete all ? ctrl+c to break" >&2
        wait
    fi
    delete "${ID}"
    exit 2
fi

# COUNT
if [ -n "$COUNT" ]; then
    get "count"
    exit 2
fi

# MOCK
if [ -n "$MOCK" ]; then
    get "mock"
    exit 2
fi

