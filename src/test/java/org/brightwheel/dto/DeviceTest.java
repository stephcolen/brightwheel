package org.brightwheel.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class DeviceTest {
  private ObjectMapper objectMapper = new ObjectMapper();

  @Test
  public void test() throws IOException {
    Device device = objectMapper.readValue(new File("src/test/resources/device.json"), Device.class);
    assertEquals("36d5658a-6908-479e-887e-a949ec199272", device.getUuid());
    List<DeviceReading> readings = device.getReadings();
    assertEquals(2, readings.size());
    assertEquals(2, readings.get(0).getCount());
    assertEquals(15, readings.get(1).getCount());
  }
}