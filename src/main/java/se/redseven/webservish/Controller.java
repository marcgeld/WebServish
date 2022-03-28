package se.redseven.webservish;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.AbstractMap;
import java.util.Base64;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
@EnableConfigurationProperties
public class Controller {

  final Logger logger = LoggerFactory.getLogger(Controller.class);

  @Autowired
  DataClient dataClient;

  private TreeMap<String, String> getBase64EncodedHeaderValues(Map<String, String> headers) {
    return headers.entrySet()
        .stream()
        .map(e -> {
          var encodedValue = Base64.getEncoder().
              encodeToString(e.getValue().getBytes(StandardCharsets.UTF_8));
          return new AbstractMap.SimpleEntry<>(e.getKey() + "_base64", encodedValue);
        }).collect(Collectors.toMap(
            Map.Entry::getKey,
            Map.Entry::getValue,
            String::concat,
            TreeMap::new));
  }

  @GetMapping(value = "/", consumes = MediaType.ALL_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Map<String, String>> requestHeaderJson(
      @RequestHeader Map<String, String> headers) {
    logger.info("<-- incomming request headers:{}", headers);
    final Map<String, String> output = getBase64EncodedHeaderValues(headers);
    output.putAll(headers);
    return ResponseEntity.ok(output);
  }

  @PostMapping(value = "/anything", consumes = MediaType.ALL_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<String> requestRelayData(@RequestHeader Map<String, String> headers,
      @RequestBody String body)
      throws IOException, InterruptedException, URISyntaxException {
    logger.info("<-- incomming request headers:{}", headers);
    logger.info("<-- incomming request body:{}", body);
    return ResponseEntity.ok(dataClient.doTransformData(body));
  }
}
