package com.mrnaif.javalab.controller;

import com.mrnaif.javalab.service.RequestCounterService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/stats")
@CrossOrigin(origins = "*")
public class RequestStatsController {

  private RequestCounterService requestCounterService;

  public RequestStatsController(RequestCounterService requestCounterService) {
    this.requestCounterService = requestCounterService;
  }

  @GetMapping
  public ResponseEntity<Integer> getRequestStats() {
    return ResponseEntity.ok(requestCounterService.getCount());
  }
}
