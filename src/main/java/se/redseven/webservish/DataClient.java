package se.redseven.webservish;

import static java.time.temporal.ChronoUnit.SECONDS;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpClient.Redirect;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.time.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

@Component
public class DataClient {

  final Logger logger = LoggerFactory.getLogger(DataClient.class);

  @Value("${application.external.service.url}")
  String applicationExternalServiceUri;

  HttpRequest createHttpRequest(String body) {
    final URI externalUri = URI.create(applicationExternalServiceUri);
    logger.info("--> outgoing URI :{}", externalUri);

    return HttpRequest.newBuilder()
        .uri(externalUri)
        .headers("Content-Type", MediaType.APPLICATION_JSON_VALUE)
        .timeout(Duration.of(10, SECONDS))
        .POST(HttpRequest.BodyPublishers.ofString(body))
        .build();
  }

  public String doTransformData(String body)
      throws URISyntaxException, IOException, InterruptedException {

    final HttpRequest request = createHttpRequest(body);
    logger.info("--> outgoing request created!");

    final var client = HttpClient.newBuilder()
        .followRedirects(Redirect.NORMAL)
        .build();
    logger.info("--> outgoing http-client created!");

    final HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
    var responseBody = String.valueOf(response.body());
    logger.info("<-- incoming data response received: {}", responseBody);
    return responseBody;
  }
}
