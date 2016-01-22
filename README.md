# IntuitCraftDemo

Document how to certify NASA's Developer API (https://api.nasa.gov/api.html#sounds) for public consumption.

## Tools Used
Rest-assured and JUnit

## Dependencies
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

- For a good query, make sure all the required fields are returned.  Also make sure the returned type are correct.  

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

|Parameter      |Type           |Can be null    |	                            |
|---------------|---------------|---------------|
|count		|int		|		|
|results	|array		|		|
|description	|string		|Yes		|
|license	|string		|		|
|title		|string		|		|
|download_url	|string		|		|
|duration	|int		|		|
|last modified	|int		|		|
|stream_url	|string		|		|
|tag_list	|string		|		|
|id		|int		|		|

Use a generated json schema for validation. See `valid-schema.json` in the resource directory.


- If a query parameter is not specified, verify the default value is used for query

|Parameter	|Type	  |Default	|Description                          |
|-----------|-------|---------|-------------------------------------|
|q	        |string	|None     |	Search text to filter results       |
|limit	    |int	  |10	      | number of tracks to return          |
|api_key	  |string |DEMO_KEY |	api.nasa.gov key for expanded usage |

Example: `GET https://api.nasa.gov/planetary/sounds`

Expected result: `Return 10 tracks by using DEMO_KEY, with no filter.`

- Validate each of the returned value has the correct value

Exampe: `download_url`
Expected result: `Basic URL validation`

Exampe: `last_modified`
Result: `In the correct date/time format`


- Validate invalid query parameters are correctly handled

Exampe: `GET https://api.nasa.gov/planetary/sounds?q=apollo&api_key=DEMO_KEY&invalidquery=321

Expected result: `Return 10 tracks by using DEMO_KEY, with no filter.`

###Parameter specific testing

####q 
- should return a list of entries containing in the description field.
- Need to verify that only entries containing q in the description fields are returned
- Need to make sure the if we don’t specify the value, it will search using the default value


####limit
- number of track to return
- Verify if limit > available track, it will return available track instead
- Need to make sure appropriate error message is returned for non positive integer query
- If query param isn’t specified, make sure it will use the default value (10).


####api_key
- Return error code / error message for invalid key
- Make sure to use DEMO_KEY as default if not specified 
- Hourly limit (30) and daily limit (50) for DEMO_KEY
