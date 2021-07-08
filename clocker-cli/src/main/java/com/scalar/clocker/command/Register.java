package com.scalar.clocker.command;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import com.scalar.clocker.exception.ClockerCliException;
import com.scalar.clocker.model.Timestamp;
import com.scalar.clocker.repository.TimestampRepository;
import java.time.LocalDateTime;
import picocli.CommandLine;
import picocli.CommandLine.Parameters;

@CommandLine.Command(
    name = Register.COMMAND_NAME,
    description = "Register a timestamp.\n"
        + "Example : register vincent sapporo",
    mixinStandardHelpOptions = true,
    version = "1.0")
public class Register implements Runnable {

  public static final String COMMAND_NAME = "register";
  private final TimestampRepository timestampRepository;
  private final ObjectMapper objectMapper;
  @Parameters()
  private String user;

  @Parameters()
  private String location;

  @Inject
  public Register(TimestampRepository timestampRepository, ObjectMapper objectMapper) {
    this.timestampRepository = timestampRepository;
    this.objectMapper = objectMapper;
  }


  @Override
  public void run() {
    LocalDateTime timestamp = LocalDateTime.now();
    Timestamp registeredTimestamp = timestampRepository.create(timestamp, user, location);
    try {
      System.out.println(
          objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(registeredTimestamp));
    } catch (
        JsonProcessingException e) {
      throw new ClockerCliException("Error marshalling the timestamp", e);
    }
  }
}
