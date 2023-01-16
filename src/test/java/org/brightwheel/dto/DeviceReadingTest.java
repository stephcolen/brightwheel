package org.brightwheel.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DeviceReadingTest {

    private ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void test() throws IOException {
        DeviceReading deviceReading1 = objectMapper.readValue(new File("src/test/resources/deviceReading1.json"), DeviceReading.class);
        assertEquals("2021-09-29T16:08:15+01:00", deviceReading1.getTimestamp());
        assertEquals(2, deviceReading1.getCount());

        DeviceReading deviceReading2 = objectMapper.readValue(new File("src/test/resources/deviceReading2.json"), DeviceReading.class);
        assertEquals("2021-09-29T16:08:15+01:00", deviceReading2.getTimestamp());
        assertEquals(2, deviceReading2.getCount());
    }
}