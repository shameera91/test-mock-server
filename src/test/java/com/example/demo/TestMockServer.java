package com.example.demo;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.jupiter.api.Test;
import org.mockserver.client.server.MockServerClient;
import org.mockserver.integration.ClientAndServer;
import org.apache.http.impl.client.HttpClientBuilder;
import org.mockserver.model.Header;
import org.mockserver.verify.VerificationTimes;

import java.util.concurrent.TimeUnit;

import static org.mockserver.integration.ClientAndServer.startClientAndServer;
import static org.mockserver.matchers.Times.exactly;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;
import static org.mockserver.model.StringBody.exact;

/**
 * Created By Shameera.A on 1/27/2023
 */
public class TestMockServer {

	private ClientAndServer mockServer;

	@BeforeClass
	public void startServer() {
		mockServer = startClientAndServer(1080);
	}

	@AfterClass
	public void stopServer() {
		mockServer.stop();
	}

	private void verifyPostRequest() {
		new MockServerClient("localhost", 1080).verify(
				request()
						.withMethod("POST")
						.withPath("/validate")
						.withBody(exact("{username: 'foo', password: 'bar'}")),
				VerificationTimes.exactly(1)
		);
	}

	@Test
	public void whenPostRequestMockServer_thenServerReceived(){
		createExpectationForInvalidAuth();
		hitTheServerWithPostRequest();
		verifyPostRequest();
	}

	private HttpResponse hitTheServerWithPostRequest() {
		String url = "http://127.0.0.1:1080/create-user";
		HttpClient client = HttpClientBuilder.create().build();
		HttpPost post = new HttpPost(url);
		post.setHeader("Content-type", "application/json");
		org.apache.http.HttpResponse response = null;

		try {
			StringEntity stringEntity = new StringEntity("{user: 'test name', password: 'test password'}");
			post.getRequestLine();
			post.setEntity(stringEntity);
			response = client.execute(post);

		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return response;
	}

	private void createExpectationForInvalidAuth() {
		new MockServerClient("127.0.0.1", 1080)
				.when(
						request()
								.withMethod("POST")
								.withPath("/validate")
								.withHeader("\"Content-type\", \"application/json\"")
								.withBody(exact("{username: 'foo', password: 'bar'}")),
						exactly(1)
				)
				.respond(
						response()
								.withStatusCode(401)
								.withHeaders(
										new Header("Content-Type", "application/json; charset=utf-8"),
										new Header("Cache-Control", "public, max-age=86400")
								)
								.withBody("{ message: 'incorrect username and password combination' }")
								.withDelay(TimeUnit.SECONDS,1)
				);
	}
}
