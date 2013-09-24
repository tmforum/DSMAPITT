


	DIRECTORIES
	***********

json
	Contains json samples played by scripts
commons
	Contains curl template for output & function ( get/post/patch etc )



	HOW TO
	******

Edit commons/conf.sh

Help :
./admin.sh -h
./ticket.sh -h



	ERROR SAMPLE
	************

error is mandatory
detail is optional
	
{
    "error":
            {
                "code": "4002",
                "title": "The status transition is not allowed"
            },
    "detail": "current=Acknowledged sent=Acknowledged"
}



	ERROR CODE
	**********
"4001", "Search query is not valid"
"4002", "The status transition is not allowed"
"4003", "Missing mandatory field"
"4004", "Unknown value"
"4041", "Unknown resource"







