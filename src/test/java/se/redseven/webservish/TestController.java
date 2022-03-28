package se.redseven.webservish;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {
  @PostMapping(value = "/localhttpbin", consumes = MediaType.ALL_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
  public String anythingTest() {
    return "{\"someKey\": 999,  \"anotherKey\": \"someData\"}";
  }
}

