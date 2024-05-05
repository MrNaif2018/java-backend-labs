package com.mrnaif.javalab.controller;

import com.mrnaif.javalab.dto.CrudStats;
import com.mrnaif.javalab.service.CrudStatsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/crud/stats")
@CrossOrigin(origins = "*")
public class CrudStatsController {

  private CrudStatsService crudStatsService;

  public CrudStatsController(CrudStatsService crudStatsService) {
    this.crudStatsService = crudStatsService;
  }

  @GetMapping
  public ResponseEntity<CrudStats> getCrudStats() {
    return ResponseEntity.ok(crudStatsService.getCrudStats());
  }
}
