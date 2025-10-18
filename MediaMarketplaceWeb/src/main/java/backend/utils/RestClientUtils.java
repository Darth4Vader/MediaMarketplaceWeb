package backend.utils;

import java.net.http.HttpClient;

import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestTemplate;

public class RestClientUtils {

	/**
	 * Create a RestClient that uses HTTP/1.1
	 * Useful for services that have issues with HTTP/2
	 * Like FastAPI sometimes
	 * @return
	 */
	public static RestClient createRestClientVersion1() {
		// Create Java 11+ HttpClient forcing HTTP/1.1
		HttpClient httpClient = HttpClient.newBuilder()
				.version(HttpClient.Version.HTTP_1_1)
				.build();

		// Wrap the HttpClient in JdkClientHttpRequestFactory for RestTemplate
		ClientHttpRequestFactory requestFactory = new JdkClientHttpRequestFactory(httpClient);

		// Create RestTemplate with custom request factory
		RestTemplate restTemplate = new RestTemplate(requestFactory);

		// Build RestClient using the custom RestTemplate
		return RestClient.builder(restTemplate)
				.build();
	}

}