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

- For a good query, make sure all the required fields are returned.  Also make sure the returned type are correct.  Use a generated json schema for validation.
```
{
  "$schema": "http://json-schema.org/draft-04/schema#",
  "id": "http://jsonschema.net",
  "type": "object",
  "properties": {
    "count": {
      "id": "http://jsonschema.net/count",
      "type": "integer"
    },
    "results": {
      "id": "http://jsonschema.net/results",
      "type": "array",
      "items": {
        "id": "http://jsonschema.net/results/0",
        "type": "object",
        "properties": {
          "description": {
            "id": "http://jsonschema.net/results/0/description",
            "type": ["string", "null"]
          },
          "license": {
            "id": "http://jsonschema.net/results/0/license",
            "type": "string"
          },
          "title": {
            "id": "http://jsonschema.net/results/0/title",
            "type": "string"
          },
          "download_url": {
            "id": "http://jsonschema.net/results/0/download_url",
            "type": "string"
          },
          "duration": {
            "id": "http://jsonschema.net/results/0/duration",
            "type": "integer"
          },
          "last_modified": {
            "id": "http://jsonschema.net/results/0/last_modified",
            "type": "string"
          },
          "stream_url": {
            "id": "http://jsonschema.net/results/0/stream_url",
            "type": "string"
          },
          "tag_list": {
            "id": "http://jsonschema.net/results/0/tag_list",
            "type": "string"
          },
          "id": {
            "id": "http://jsonschema.net/results/0/id",
            "type": "integer"
          }
        },
		"required": ["description", "license", "title", "download_url", "duration", "last_modified", "stream_url", "tag_list", "id"]
      },
      "additionalItems": false
    }
  },
  "required": [
    "count",
    "results"
  ]
}
```

- If a query parameter is not specified, verify the default value is used for query

|Parameter	|Type	  |Default	|Description                          |
|-----------|-------|---------|-------------------------------------|
|q	        |string	|None     |	Search text to filter results       |
|limit	    |int	  |10	      | number of tracks to return          |
|api_key	  |string |DEMO_KEY |	api.nasa.gov key for expanded usage |

Example: `GET https://api.nasa.gov/planetary/sounds`

Expected result: `Return 10 tracks by using DEMO_KEY, with no filter.`


Need to handle invalid query parameters
(example query + expected results)

###Parameter specific testing

q - search test to filter result
should return a list of entries containing in the description field.
Need to verify that only entries containing q in the description fields are returned
Need to make sure the if we don’t specify the value, it will search using the default value


2. limit
number of track to return

Verify if limit > available track, it will return available track instead
Need to make sure appropriate error message is returned for non positive integer query
If query param isn’t specified, make sure it will use the default value (10).


3. api_key

Return error code / error message for invalid key
Make sure to use DEMO_KEY as default if not specified 
Hourly limit (30) and daily limit (50) for DEMO_KEY
