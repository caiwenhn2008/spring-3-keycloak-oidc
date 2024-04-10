package com.edw.controller;

import org.springframework.http.ResponseEntity;

import java.util.List;

public class KeyClock extends ApiBinding{
  private static final String GRAPH_API_BASE_URL =
    "http://localhost:9090";

  public KeyClock(String accessToken) {
    super(accessToken);
  }

  public Workout[] getWorkout() {
    ResponseEntity<Workout[]> responseEntity = restTemplate.getForEntity(
      GRAPH_API_BASE_URL + "/workout/", Workout[].class);
    Workout[] workouts = responseEntity.getBody();
    return workouts;
  }
}
