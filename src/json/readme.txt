


	DIRECTORIES
	***********

../json
	Contains json samples played by scripts
commons
	Contains curl template for output & function ( get/post/patch etc )



	HOW TO
	******

	- create TT
create-tt.sh

	- List All TT
./list-tt.sh

	- List All TT with attribute selection
./list-tt.sh ${attribute} with attribute=field1,field2,field3,...

	- Get single TT resource
./get-tt.sh ${id}	

	- Get single TT resource with attribute selection
./get-tt.sh ${id} ${attribute} with attribute=field1,field2,field3,...

	- Partial update
./patch-tt.sh ${id}

	- Full update
./put-tt.sh ${id}

	- Count TT
./admin-count-tt.sh

	- Delete TT
./admin-delete-single-tt ${id}

	- Delete all TT
./admin-delete-all-tt.sh

	- Create 10 TT
./admin-create-tt-10.sh

	- Get a mock TT
./admin-mock-tt.sh



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







