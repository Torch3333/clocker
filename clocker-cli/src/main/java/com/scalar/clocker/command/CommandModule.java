package com.scalar.clocker.command;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.scalar.clocker.exception.ClockerCliException;
import com.scalar.clocker.repository.TimestampRepository;
import com.scalar.db.config.DatabaseConfig;
import com.scalar.db.service.TransactionModule;
import com.scalar.db.service.TransactionService;
import java.io.IOException;
import java.io.InputStream;

public class CommandModule extends AbstractModule {

  @Provides
  @Singleton
  TransactionService provideDatabaseService() {
    try {
      InputStream config =
          this.getClass().getClassLoader().getResourceAsStream("scalardb.properties");
      Injector injector = Guice.createInjector(new TransactionModule(new DatabaseConfig(config)));
      return injector.getInstance(TransactionService.class);
    } catch (IOException e) {
      throw new ClockerCliException("Error initializing Scalar DB configuration", e);
    }
  }

  @Provides
  @Singleton
  @Inject
  TimestampRepository provideRepository(TransactionService db) {
    return new TimestampRepository(db);
  }

  @Provides
  @Singleton
  /**
   * Used to print POJO as user readable JSON
   */
  ObjectMapper provideObjectMapper() {
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.findAndRegisterModules();
    objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    return objectMapper;
  }
}
