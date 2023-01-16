package org.brightwheel;

import org.brightwheel.dto.Device;
import org.brightwheel.dto.DeviceReading;
import org.brightwheel.requests.GetCumulativeCountRequest;
import org.brightwheel.requests.GetCumulativeCountResponse;
import org.brightwheel.requests.GetLatestReadingRequest;
import org.brightwheel.requests.GetLatestReadingResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/")
public class DeviceController {

  @Autowired DeviceService deviceService;

  @GetMapping("/cumulative-count")
  public GetCumulativeCountResponse getCumulativeCount(@RequestBody GetCumulativeCountRequest getCumulativeCountRequest) {
    Integer cumulativeCount = deviceService.getCumulativeCount(getCumulativeCountRequest.getUuid());
    return GetCumulativeCountResponse.builder()
        .cumulativeCount(cumulativeCount)
        .build();
  }

  @GetMapping("/latest-reading")
  public GetLatestReadingResponse getLatestReading(@RequestBody GetLatestReadingRequest getLatestReadingRequest) {
    Optional<DeviceReading> deviceReading = deviceService.getLatestReading(getLatestReadingRequest.getUuid());
    return deviceReading.isPresent() ?
        GetLatestReadingResponse.builder()
            .latestTimestamp(deviceReading.get().getTimestamp())
            .build() :
        GetLatestReadingResponse.builder().build();
  }

    @PostMapping("/create")
    public Device create(@RequestBody Device device) {
      deviceService.create(device);
      return device;
    }
}
