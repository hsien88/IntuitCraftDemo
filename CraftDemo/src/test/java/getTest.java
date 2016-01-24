import static com.jayway.restassured.RestAssured.baseURI;
import static com.jayway.restassured.RestAssured.given;
import static com.jayway.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static com.jayway.restassured.path.json.JsonPath.from;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.lessThan;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class getTest {

	final int DEMO_API_RATE_LIMIT = 30;
	final int DEVELOPER_API_RATE_LIMIT = 1000;
	final int DEFAULT_LIMIT_SIZE = 10;
	final String DEMO_API_KEY_NAME = "DEMO_KEY";
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		
		baseURI = "https://api.nasa.gov/planetary/sounds";
	}

	@After
	public void tearDown() throws Exception {
	}
	
	/**
	 * Check if the GET query returns in less than 2000 ms
	 */
	@Test
	public void responseTimeTest1() {
		given().
	        queryParam("api_key", "DEMO_KEY").
	    when().
	        get().
	    then().
	    	log().all().
	    	statusCode(200).
	    	time(lessThan(2000L));
	}
	
	/**
	 * Check the last_modified field is in the correct 
	 * date - time format
	 */
	@Test
	public void lastModifiedTest1() {
		String response = given().
			queryParam("limit", "10").
        	queryParam("api_key", "DEMO_KEY").
        when().
        	get().
        then().
	        log().all().
	        statusCode(200).
	        body(matchesJsonSchemaInClasspath("sound-schema.json")).
	        extract().response().asString();
		
		List<String> dates = from(response).getList("results.last_modified");
		
		for (String modifiedDate : dates) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss Z");
			sdf.setLenient(false);
			
			//if not valid, it will throw ParseException
			Date date;
			try {
				date = sdf.parse(modifiedDate);
			} catch (java.text.ParseException e) {
				System.out.println(e.toString());
				fail("last_modified date is not in the correct format: " + modifiedDate);
			}
		}
	}
	
	/**
	 * Check if the API can handle invalid parameters
	 */
	@Test
	public void invalidParamTest1() {
		given().
	        queryParam("invalid_paramXYZ", "10").
	        // Note: currently there is a bug in the GET API where we have to specify the api_key value
	        queryParam("api_key", "DEMO_KEY").
	    when().
	        get().
	    then().
	        log().all().
	        statusCode(200).
	        body(matchesJsonSchemaInClasspath("sound-schema.json")).
	        body("count", equalTo(DEFAULT_LIMIT_SIZE)).
	        body("results", hasSize(DEFAULT_LIMIT_SIZE));
	}
	
	/**
	 * Check if the default values are used for query parameters if not specified
	 */
	@Test
	public void defaultValueTest1() {
		given().
	        // NOTE: currently there is a bug in the GET API where we have to specify the api_key value
	        queryParam("api_key", "DEMO_KEY").
	    when().
	        get().
	    then().
	        log().all().
	        statusCode(200).
	        body(matchesJsonSchemaInClasspath("sound-schema.json")).
	        body("count", equalTo(DEFAULT_LIMIT_SIZE)).
	        body("results", hasSize(DEFAULT_LIMIT_SIZE));
	}
	
	
	/**
	 * Check if the rate limit value is correct for api_key - DEMO_KEY
	 */
	@Test
	public void rateLimitTest1() {
		String myHeaderValue = given().
	        queryParam("limit", "1").
	        queryParam("api_key", "DEMO_KEY").
	    when().
	        get().
	    then().
	    	log().all().
	    	statusCode(200).
	    	extract().headers().getValue("X-RateLimit-Limit");
				
		assertThat(Integer.parseInt(myHeaderValue), equalTo(DEMO_API_RATE_LIMIT));
	}
	
	/**
	 * Check if query parameter Q returns the correct response
	 */
	@Test
	public void queryParameterQTest1() {
		
	    String response = given().
	    	queryParam("q", "Voyager").
	        queryParam("limit", "1").
	        queryParam("api_key", "DEMO_KEY").
	    when().
	        get().
	    then().
	        log().all().
	        statusCode(200).
	        body(matchesJsonSchemaInClasspath("sound-schema.json")).
	        extract().response().asString();
	    
	    List<String> descriptions = from(response).getList("results.description");
	    
	    for (String s : descriptions) {
	    	assertThat(s, containsString("Voyager"));
	    }    
	}
}
