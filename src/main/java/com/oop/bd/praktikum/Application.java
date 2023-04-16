package com.oop.bd.praktikum;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class Application {

  public static void main(String[] args) {
    System.setProperty("java.awt.headless", "false");
    ConfigurableApplicationContext context = SpringApplication.run(Application.class, args);

    PraktikumApplication praktikumApplication = context.getBean(PraktikumApplication.class);
    praktikumApplication.run();
  }

}
