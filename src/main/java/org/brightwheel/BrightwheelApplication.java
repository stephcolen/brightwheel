package org.brightwheel;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import static org.brightwheel.DeviceService.READINGS;

@SpringBootApplication
public class BrightwheelApplication {
  public static void main(String[] args) {
    READINGS.clear();
    SpringApplication.run(BrightwheelApplication.class, args);
  }
}