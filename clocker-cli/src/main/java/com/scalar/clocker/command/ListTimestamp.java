package com.scalar.clocker.command;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import com.scalar.clocker.exception.ClockerCliException;
import com.scalar.clocker.model.Timestamp;
import com.scalar.clocker.repository.TimestampRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.logging.Logger;
import picocli.CommandLine;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

@CommandLine.Command(
    name = ListTimestamp.COMMAND_NAME,
    description = "List registered timestamps for the given date. \n"
        + "Example : list 2021-07-08",
    mixinStandardHelpOptions = true,
    version = "1.0")

public class ListTimestamp implements Runnable {

  public static final String COMMAND_NAME = "list";
  public static final Logger logger = Logger.getLogger(ListTimestamp.class.getSimpleName());
  private final TimestampRepository timestampRepository;
  private final ObjectMapper objectMapper;
  @Parameters
  private LocalDate date;
  @Option(
      names = {"-u", "--user"},
      description = "User name")
  private String user;

  @Inject
  public ListTimestamp(TimestampRepository timestampRepository, ObjectMapper objectMapper) {
    this.timestampRepository = timestampRepository;
    this.objectMapper = objectMapper;
  }

  @Override
  public void run() {
    List<Timestamp> timestamps = timestampRepository.readAll(date, user);
    try {
      System.out
          .println(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(timestamps));
    } catch (JsonProcessingException e) {
      throw new ClockerCliException("Error marshalling the timestamps", e);
    }
  }
}
