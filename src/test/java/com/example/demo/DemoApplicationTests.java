package com.example.demo;

import com.jayway.restassured.RestAssured;
import com.jayway.restassured.http.ContentType;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.hamcrest.CoreMatchers.equalTo;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class DemoApplicationTests {

	@Test
	void demoTest() {
		RestAssured.given().contentType(ContentType.JSON)
				.get("https://api.tmsandbox.co.nz/v1/Categories/6327/Details.json?catalogue=false").then()
				.statusCode(HttpStatus.SC_OK)
				.body("Name",equalTo("Carbon credits"))
				.body("CanRelist",equalTo(true))
				.body("Promotions.find{it.Name == 'Gallery'}.Description",equalTo("Good position in category"));
	}
}
