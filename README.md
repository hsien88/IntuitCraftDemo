# Intuit Craft Demo

Document how to certify NASA's Developer API (GET https://api.nasa.gov/api.html#sounds) for public consumption.

## Tools Used
Rest-assured and JUnit

## Maven Dependencies
```
<dependency>
    <groupId>com.jayway.restassured</groupId>
    <artifactId>rest-assured</artifactId>
    <version>2.8.0</version>
</dependency>

<dependency>
    <groupId>org.hamcrest</groupId>
    <artifactId>hamcrest-all</artifactId>
    <version>1.3</version>
</dependency>

<dependency>
    <groupId>junit</groupId>
    <artifactId>junit</artifactId>
    <version>4.11</version>
</dependency>
```

## Test

* Verify all the required fields are returned and the type for each field is correct.  

Sample Response
```
{
count: 10,
results: [
{
description: "The Voyager 1 spacecraft has experienced three "tsunami waves" in interstellar space. Listen to how these waves cause surrounding ionized matter to ring. More details on this sound can be found here: http://www.nasa.gov/jpl/nasa-voyager-tsunami-wave-still-flies-through-interstellar-space/",
license: "cc-by-nc-sa",
title: "Voyager 1: Three "Tsunami Waves" in Interstellar Space",
download_url: "https://api.soundcloud.com/tracks/181835738/download",
duration: 18365,
last_modified: "2014/12/16 22:34:23 +0000",
stream_url: "https://api.soundcloud.com/tracks/181835738/stream",
tag_list: "Space",
id: 181835738
},
...
```
Required return fields and types

|Parameter      |Type           |	                            
|---------------|---------------|
|count		|int		|		
|results	|array		|		
|description	|string	|
|license	|string		|		
|title		|string		|		
|download_url	|string		|		
|duration	|int		|		
|last modified	|int		|		
|stream_url	|string		|		
|tag_list	|string		|		
|id		|int		|		

Use a generated json schema for validation. See `valid-schema.json` in the resource directory.

* Verify the default value is used for a query if it's not specified.

|Parameter	|Type	  |Default	|Description                          |
|-----------|-------|---------|-------------------------------------|
|q	        |string	|None     |	Search text to filter results       |
|limit	    |int	  |10	      | number of tracks to return          |
|api_key	  |string |DEMO_KEY |	api.nasa.gov key for expanded usage |
-------------------------------------------------------------------------
- Example: `GET https://api.nasa.gov/planetary/sounds`
- Expected Result: `Return 10 tracks by using DEMO_KEY, with no filter.`

* Verify each of the returned value has the expected format.
     -Exampe: `download_url`
     -Expected Result: `In valid URL format`

    - Exampe: `last_modified`
    - Result: `In valid date/time format`

* Verify invalid query parameters are correctly handled.
    - Exampe: `GET https://api.nasa.gov/planetary/sounds?q=apollo&api_key=DEMO_KEY&invalidquery=321
    - Expected Result: `Return 10 tracks by using DEMO_KEY, with no filter.`

* Verify the perofrmance for a query is acceptable.
    - Example: `GET https://api.nasa.gov/planetary/sounds`
    - Expected Result: `Should return in less than X seconds`

###Parameter Specific Testings

####q 
* Verify the value specified in q are in the returned description fields.
    - Exampe: `GET https://api.nasa.gov/planetary/sounds?q="Voyager"
    - Expected Result: `description: "The Voyager 1 spacecraft...`

* Verify only entries containing q in the description fields are returned.
    - Example: `GET https://api.nasa.gov/planetary/sounds?q="Voyager"
    - Expected result: `All the results entries should contain "Voyager" in its description`


####limit
* Verify limit value specified equals to the number of entries returned.
    - Example: `GET https://api.nasa.gov/planetary/sounds?limit=5`
    - Expected Result: `count: 5` and `5 entires in the results array`

* Verify if limit > available tracks, it will return available track instead.
    - Example: `GET https://api.nasa.gov/planetary/sounds?limit=999999999999`
    - Expected result: `count: 64`, when there are 64 sound tracks available on the server.

* Verify correct http status code and error message are returned for invalid/negative limit value
    - Example: `GET https://api.nasa.gov/planetary/sounds?limit="-10"`
    - Expected result: `HTTP status code 4xx`


####api_key
* Verify correct http error code and error message are returned for invalid api_key value
    - Example: `GET https://api.nasa.gov/planetary/sounds?api_key="invalid_key"`
    - Expected result: `HTTP status code 403` and message `An invalid api_key was supplied. Get one at https://api.nasa.gov`

* Verify the values for hourly limit and daily limit are correct for the different API keys
    - Example: `GET https://api.nasa.gov/planetary/sounds?api_key=DEMO_KEY`
    - Expected result: `X-RateLimit-Limit: 30` in the http header.

* Verify correct http status code and error message are returned when the daily/hourly limit has been 
    - Example: `GET https://api.nasa.gov/planetary/sounds?api_key=DEMO_KEY` when `X-RateLimit-Remaining = 0`
    - Expected result: `HTTP status code 403`

