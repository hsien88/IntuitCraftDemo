import static com.jayway.restassured.RestAssured.baseURI;
import static com.jayway.restassured.RestAssured.given;
import static com.jayway.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.hasValue;
import static org.junit.Assert.assertThat;

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
	        body(matchesJsonSchemaInClasspath("valid-schema.json")).
	        body("count", hasValue(DEFAULT_LIMIT_SIZE)).
	        body("results", hasSize(DEFAULT_LIMIT_SIZE));
	}
	
	@Test
	public void defaultValueTest1() {
		given().
	        // Note: currently there is a bug in the GET API where we have to specify the api_key value
	        queryParam("api_key", "DEMO_KEY").
	    when().
	        get().
	    then().
	        log().all().
	        statusCode(200).
	        body(matchesJsonSchemaInClasspath("valid-schema.json")).
	        body("count", hasValue(DEFAULT_LIMIT_SIZE)).
	        body("results", hasSize(DEFAULT_LIMIT_SIZE));
	}
	
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
	
	@Test
	public void myFirstRestAssuredTest() {
		
	    given().
	        queryParam("limit", "10").
	        queryParam("api_key", "DEMO_KEY").
	    when().
	        get().
	    then().
	        log().all().
	        statusCode(200).
	        body(matchesJsonSchemaInClasspath("valid-schema.json")).
	        body("results.findall.description", contains("a"));
	    
	}
}
