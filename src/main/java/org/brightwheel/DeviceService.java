package org.brightwheel;

import org.brightwheel.dto.Device;
import org.brightwheel.dto.DeviceReading;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class DeviceService {
  private static final Comparator<DeviceReading> COMPARATOR_BY_TIMESTAMP = Comparator.comparing(DeviceReading::getTimestamp).reversed();
  static final Map<String, Set<DeviceReading>> READINGS = new HashMap<>();

  public Integer getCumulativeCount(String uuid) {
    return READINGS.getOrDefault(uuid, new TreeSet<>(COMPARATOR_BY_TIMESTAMP)).stream()
        .map(DeviceReading::getCount)
        .reduce(0, Integer::sum);
  }

  public Optional<DeviceReading> getLatestReading(String uuid) {
    return READINGS.getOrDefault(uuid, new TreeSet<>(COMPARATOR_BY_TIMESTAMP)).stream()
        .findFirst();
  }

  public Device create(Device device) {
    READINGS.computeIfAbsent(device.getUuid(), key -> new TreeSet<>(COMPARATOR_BY_TIMESTAMP)).addAll(device.getReadings());
    return device;
  }
}
