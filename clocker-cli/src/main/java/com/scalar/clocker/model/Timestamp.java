package com.scalar.clocker.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class Timestamp {
  public static final String DATE = "date";
  public static final String TIME = "time";
  public static final String USER = "user";
  public static final String LOCATION = "location";
  public static final String CREATED_AT = "created_at";
  LocalDate date;
  LocalTime time;
  String user;
  String location;
  LocalDateTime createdAt;
}
