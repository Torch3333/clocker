package com.scalar.clocker.repository;


import com.scalar.clocker.exception.ClockerCliException;
import com.scalar.clocker.exception.RepositoryException;
import com.scalar.clocker.model.Timestamp;
import com.scalar.db.api.DistributedTransaction;
import com.scalar.db.api.DistributedTransactionManager;
import com.scalar.db.api.Get;
import com.scalar.db.api.Put;
import com.scalar.db.api.Result;
import com.scalar.db.api.Scan;
import com.scalar.db.exception.transaction.TransactionException;
import com.scalar.db.io.BigIntValue;
import com.scalar.db.io.Key;
import com.scalar.db.io.TextValue;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class TimestampRepository extends ScalarDbReadOnlyTransactionRepository<Timestamp> {

  public static final String NAMESPACE = "clocker";
  public static final String TABLE_NAME = "timestamp";
  private final DistributedTransactionManager db;

  public TimestampRepository(DistributedTransactionManager db) {
    this.db = db;
  }

  public Timestamp create(LocalDateTime localDateTime, String user, String location) {
    try {
      DistributedTransaction tx = db.start();
      Get get = createGet(localDateTime.toLocalDate(), localDateTime.toLocalTime(), user);
      getAndThrowsIfAlreadyExist(tx, get, Timestamp.class);

      Put put =
          new Put(
              createPk(localDateTime.toLocalDate()),
              createCk(localDateTime.toLocalTime(), user))
              .withValues(
                  Arrays.asList(
                      new TextValue(Timestamp.LOCATION, location),
                      new BigIntValue(
                          Timestamp.CREATED_AT,
                          localDateTime.toInstant(ZoneOffset.UTC).toEpochMilli())))
              .forNamespace(NAMESPACE)
              .forTable(TABLE_NAME);
      tx.put(put);
      tx.commit();

      return Timestamp.builder().time(localDateTime.toLocalTime()).date(localDateTime.toLocalDate())
          .location(location).createdAt(localDateTime).user(user).build();
    } catch (TransactionException e) {
      throw new ClockerCliException("Error creating timestamp", e);
    }
  }

  public List<Timestamp> readAll(LocalDate date, @Nullable String user) {
    DistributedTransaction tx = null;
    try {
      tx = db.start();
    } catch (TransactionException e) {
      throw new RepositoryException("Error when reading all the timestamp");
    }
    Scan scan = new Scan(createPk(date)).forNamespace(NAMESPACE).forTable(TABLE_NAME);

    List<Timestamp> timestamps = scan(tx, scan);
    if (user != null) {
      return timestamps.stream().filter(t -> t.getUser().equals(user)).collect(Collectors.toList());
    }
    return timestamps;
  }

  private Get createGet(LocalDate date, LocalTime time, String user) {
    return new Get(createPk(date), createCk(time, user))
        .forNamespace(NAMESPACE)
        .forTable(TABLE_NAME);
  }

  private Key createPk(LocalDate date) {
    return new Key(new TextValue(Timestamp.DATE, date.toString()));
  }

  private Key createCk(LocalTime time, String user) {
    return new Key(
        new TextValue(Timestamp.TIME, time.toString()), new TextValue(Timestamp.USER, user));
  }

  @Override
  Timestamp parse(@Nonnull Result result) {
    Timestamp.TimestampBuilder builder = Timestamp.builder();
    builder.date(
        LocalDate.parse(((TextValue) result.getValue(Timestamp.DATE).get()).getString().get()));
    builder.time(
        LocalTime.parse(((TextValue) result.getValue(Timestamp.TIME).get()).getString().get()));
    Util.setTextValueIfPresent(result, Timestamp.USER, builder::user);
    Util.setTextValueIfPresent(result, Timestamp.LOCATION, builder::location);
    builder.createdAt(
        LocalDateTime.ofInstant(
            Instant.ofEpochMilli(((BigIntValue) result.getValue(Timestamp.CREATED_AT).get()).get()),
            ZoneOffset.UTC));
    return builder.build();
  }
}
