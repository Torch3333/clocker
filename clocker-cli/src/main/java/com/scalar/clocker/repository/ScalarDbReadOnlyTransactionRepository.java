package com.scalar.clocker.repository;



import com.scalar.clocker.exception.ObjectAlreadyExistingException;
import com.scalar.clocker.exception.ObjectNotFoundException;
import com.scalar.clocker.exception.RepositoryException;
import com.scalar.db.api.DistributedTransaction;
import com.scalar.db.api.Get;
import com.scalar.db.api.Result;
import com.scalar.db.api.Scan;
import com.scalar.db.exception.transaction.CrudException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;

/**
 * A generic repository base class for read-only purpose of a Scalar DB table used in the
 * transaction mode
 *
 * @param <T> the data class
 */
public abstract class ScalarDbReadOnlyTransactionRepository<T> {

  public Optional<T> get(DistributedTransaction tx, Get get) throws RepositoryException {
    try {
      Optional<Result> result = tx.get(get);
      return result.map(this::parse);
    } catch (CrudException e) {
      throw new RepositoryException("Error reading the record for " + get, e);
    }
  }

  public T getAndThrowsIfNotFound(DistributedTransaction tx, Get get, Class<T> clazz)
      throws ObjectNotFoundException, RepositoryException {
    try {
      Result result =
          tx.get(get)
              .orElseThrow(
                  () ->
                      new ObjectNotFoundException(
                          clazz,
                          get.getPartitionKey().toString(),
                          get.getClusteringKey().toString()));
      return this.parse(result);
    } catch (CrudException e) {
      throw new RepositoryException("Error reading the record for " + get, e);
    }
  }

  public void getAndThrowsIfAlreadyExist(DistributedTransaction tx, Get get, Class<T> clazz)
      throws ObjectAlreadyExistingException, RepositoryException {
    try {
      if (tx.get(get).isPresent()) {
        throw new ObjectAlreadyExistingException(
            clazz, get.getPartitionKey().toString(), get.getClusteringKey().toString());
      }
    } catch (CrudException e) {
      throw new RepositoryException("Error reading the record for " + get, e);
    }
  }

  public List<T> scan(DistributedTransaction tx, Scan scan) throws RepositoryException {
    try {
      List<Result> results = tx.scan(scan);
      return results.stream().map(this::parse).collect(Collectors.toList());
    } catch (CrudException e) {
      throw new RepositoryException("Error scanning the record for " + scan, e);
    }
  }

  public List<T> scanAndThrowsIfNotFound(DistributedTransaction tx, Scan scan, Class<T> clazz)
      throws ObjectNotFoundException, RepositoryException {
    try {
      List<Result> results = tx.scan(scan);
      if (results.isEmpty()) {
        throw new ObjectNotFoundException(clazz, scan.getPartitionKey().toString());
      }
      return results.stream().map(this::parse).collect(Collectors.toList());
    } catch (CrudException e) {
      throw new RepositoryException("Error scanning the record for " + scan, e);
    }
  }

  /**
   * Convert a Scalar DB query result to an object of the data class
   *
   * @param result the Scalar DB query result
   * @return an object of the data class
   */
  abstract T parse(@Nonnull Result result);
}

