package com.edw.controller;

import java.time.LocalDateTime;

public class Workout {

    private int id;
    private String user;
    private LocalDateTime start;
    private LocalDateTime end;
    private int difficulty;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public LocalDateTime getStart() {
        return start;
    }

    public void setStart(LocalDateTime start) {
        this.start = start;
    }

    public LocalDateTime getEnd() {
        return end;
    }

    public void setEnd(LocalDateTime end) {
        this.end = end;
    }

    public int getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(int difficulty) {
        this.difficulty = difficulty;
    }

    @Override
    public String toString() {
        return "Workout{" +
          "id=" + id +
          ", user='" + user + '\'' +
          ", start=" + start +
          ", end=" + end +
          ", difficulty=" + difficulty +
          '}';
    }
}
