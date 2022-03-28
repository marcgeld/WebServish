package se.redseven.webservish;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class WebServishApplicationTests {

  final Logger logger = LoggerFactory.getLogger(WebServishApplicationTests.class);

  @Autowired
  private Controller controller;

  @LocalServerPort
  private int port;

  private HttpResponse<String> getFromServer(URI uri) throws IOException, InterruptedException {
    HttpClient client = HttpClient.newHttpClient();
    HttpRequest request = HttpRequest.newBuilder()
        .uri(uri)
        //.version(Version.HTTP_1_1)
        .header("Accept", "application/json")
        .GET()
        .build();

    return client.send(request, BodyHandlers.ofString());
  }

  private HttpResponse<String> postToServer(URI uri) throws IOException, InterruptedException {
    HttpClient client = HttpClient.newHttpClient();
    HttpRequest request = HttpRequest.newBuilder()
        .uri(uri)
        //.version(Version.HTTP_1_1)
        .header("Accept", "application/json")
        .POST(HttpRequest.BodyPublishers.ofString("Sample request body"))
        .build();

    return client.send(request, BodyHandlers.ofString());
  }

  @Test
  void contextLoads() {
    assertThat(controller).isNotNull();
  }

  @Test
  void webShouldReturnLocalMessage() throws Exception {
    // HttpClient --> Controller[/]
    final URI localUri = URI.create(String.format("http://localhost:%s/", port));
    logger.info("Accessing local api URI:{}", localUri);
    HttpResponse<String> httpResponse = getFromServer(localUri);
    assertThat(httpResponse.statusCode()).isEqualTo(200);
  }

  @Test
  void webShouldRejectHttpGETMethod() throws Exception {
    // HttpClient --> Controller[/anything]
    final URI localUri = URI.create(String.format("http://localhost:%s/anything", port));
    logger.info("Accessing local api URI:{}", localUri);
    HttpResponse<String> httpResponse = getFromServer(localUri);
    assertThat(httpResponse.statusCode()).isEqualTo(405);
    logger.info("<-- body: {}", httpResponse.body());
  }

  @Test
  void webShouldReturnProxyMessage() throws Exception {
    // HttpClient --> Controller[/anything] --> DataClient --> TestController[/localhttpbin]
    final URI dataClientUrl = URI.create(String.format("http://localhost:%s/localhttpbin", port));
    logger.info("Set DataClient url to: {}", dataClientUrl.toASCIIString());
    controller.dataClient.applicationExternalServiceUri = dataClientUrl.toString();
    final URI localUri = URI.create(String.format("http://localhost:%s/anything", port));
    logger.info("Accessing local api URI:{}", localUri);
    HttpResponse<String> httpResponse = postToServer(localUri);
    assertThat(httpResponse.statusCode()).isEqualTo(200);
    logger.info("<-- body: {}", httpResponse.body());
  }
}
