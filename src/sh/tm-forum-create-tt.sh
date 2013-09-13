#!/bin/bash

set -e

. common.sh

NOLOG=true

section "Step 1 - Checking existing quota-related Resources"

infile=$(mktemp)

start="$(TZ="Zulu" date +'%FT%T.000Z')"
end="$(TZ="Zulu" date +'%FT%T.000Z' --date "@$(($(date +'%s') + 60 * 60 * 24 * 7))")"

cat > $infile <<EOF
{
	"resourceURI": "http://schemas.dmtf.org/cimi/cdcc/QuotaPackage",
	"name": "Quota Package Medium",
	"description": "Medium sized quota package",
	"start": "${start}",
	"end": "${end}"
}
EOF

post "quotaPackages" $infile

quotaPackage="$(sed -n 's/^< Location: \(.*[0-9]\)\s*$/\1/p' $errfile)"
#echo "QuotaPackage: $quotaPackage"

cat > $infile <<EOF
{
	"resourceURI": "http://schemas.dmtf.org/cimi/cdcc/ResourceQuota",
	"name": "CPU Limit",
	"description": "The maximum number of virtual CPUs",
	"quotaResources": [
		{
			"resourceType": "http://schemas.dmtf.org/cimi/1/Machine",
			"resourceAttribute": "cpu"
		}
	],
	"quotaValue": "4",
	"unit": "count",
	"quotaPackage": {
		 "href": "${quotaPackage}"
	}
}
EOF

post "resourceQuotas" $infile

cat > $infile <<EOF
{
	"resourceURI": "http://schemas.dmtf.org/cimi/cdcc/ResourceQuota",
	"name": "RAM Limit",
	"description": "The maximum amount of virtual memory",
	"quotaResources": [
		{
			"resourceType": "http://schemas.dmtf.org/cimi/1/Machine",
			"resourceAttribute": "memory"
		}
	],
	"quotaValue": "32.0",
	"unit": "gibibyte",
	"quotaPackage": {
		 "href": "${quotaPackage}"
	}
}
EOF

post "resourceQuotas" $infile

cat > $infile <<EOF
{
	"resourceURI": "http://schemas.dmtf.org/cimi/cdcc/ResourceQuota",
	"name": "Storage Limit",
	"description": "The maximum amount of virtual storage",
	"quotaResources": [
		{
			"resourceType": "http://schemas.dmtf.org/cimi/1/Disk",
			"resourceAttribute": "capacity"
		},
		{
			"resourceType": "http://schemas.dmtf.org/cimi/1/Volume",
			"resourceAttribute": "capacity"
		}
	],
	"quotaValue": "2.0",
	"unit": "terabyte",
	"quotaPackage": {
		 "href": "${quotaPackage}"
	}
}
EOF

post "resourceQuotas" $infile

cat > $infile <<EOF
{
	"resourceURI": "http://schemas.dmtf.org/cimi/cdcc/ResourceQuota",
	"name": "Network Limit",
	"description": "The maximum number of virtual networks",
	"quotaResources": [
		{
			"resourceType": "http://schemas.dmtf.org/cimi/1/Network"
		}
	],
	"quotaValue": "2",
	"unit": "count",
	"quotaPackage": {
		 "href": "${quotaPackage}"
	}
}
EOF

post "resourceQuotas" $infile

NOLOG=
get "quotaPackages"
cat $outfile

get "$quotaPackage?\$expand=resourceQuotas"
cat $outfile

NOLOG=true

sleep 1
section "Step 2 - Check new quota-related Resources"

start="$(TZ="Zulu" date +'%FT%T.000Z')"
end="$(TZ="Zulu" date +'%FT%T.000Z' --date "@$(($(date +'%s') + 60 * 60 * 24 * 7))")"

cat > $infile <<EOF
{
	"resourceURI": "http://schemas.dmtf.org/cimi/cdcc/QuotaPackage",
	"name": "Quota Package Small",
	"description": "Small sized quota package",
	"start": "${start}",
	"end": "${end}"
}
EOF

post "quotaPackages" $infile

quotaPackage="$(sed -n 's/^< Location: \(.*[0-9]\)\s*$/\1/p' $errfile)"
#echo "QuotaPackage: $quotaPackage"

cat > $infile <<EOF
{
	"resourceURI": "http://schemas.dmtf.org/cimi/cdcc/ResourceQuota",
	"name": "CPU Limit",
	"description": "The maximum number of virtual CPUs",
	"quotaResources": [
		{
			"resourceType": "http://schemas.dmtf.org/cimi/1/Machine",
			"resourceAttribute": "cpu"
		}
	],
	"quotaValue": "2",
	"unit": "count",
	"quotaPackage": {
		 "href": "${quotaPackage}"
	}
}
EOF

post "resourceQuotas" $infile

cat > $infile <<EOF
{
	"resourceURI": "http://schemas.dmtf.org/cimi/cdcc/ResourceQuota",
	"name": "RAM Limit",
	"description": "The maximum amount of virtual memory",
	"quotaResources": [
		{
			"resourceType": "http://schemas.dmtf.org/cimi/1/Machine",
			"resourceAttribute": "memory"
		}
	],
	"quotaValue": "4.0",
	"unit": "gibibyte",
	"quotaPackage": {
		 "href": "${quotaPackage}"
	}
}
EOF

post "resourceQuotas" $infile

cat > $infile <<EOF
{
	"resourceURI": "http://schemas.dmtf.org/cimi/cdcc/ResourceQuota",
	"name": "Storage Limit",
	"description": "The maximum amount of virtual storage",
	"quotaResources": [
		{
			"resourceType": "http://schemas.dmtf.org/cimi/1/Disk",
			"resourceAttribute": "capacity"
		},
		{
			"resourceType": "http://schemas.dmtf.org/cimi/1/Volume",
			"resourceAttribute": "capacity"
		}
	],
	"quotaValue": "250.0",
	"unit": "gigabyte",
	"quotaPackage": {
		 "href": "${quotaPackage}"
	}
}
EOF

post "resourceQuotas" $infile

NOLOG=
get "quotaPackages"
cat $outfile

get "$quotaPackage?\$expand=resourceQuotas"
cat $outfile


rm -f $errfile $outfile
