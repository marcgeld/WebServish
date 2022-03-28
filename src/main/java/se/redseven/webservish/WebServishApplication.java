package se.redseven.webservish;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class WebServishApplication {

  static final Logger logger = LoggerFactory.getLogger(WebServishApplication.class);

  public static void main(String[] args) {
    logger.info("starting app…");
    SpringApplication.run(WebServishApplication.class, args);
  }
}
