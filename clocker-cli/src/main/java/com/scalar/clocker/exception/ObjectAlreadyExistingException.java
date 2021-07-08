package com.scalar.clocker.exception;

/**
 * Exception thrown when an object cannot be found in the database
 */
public class ObjectAlreadyExistingException extends ObjectExistsException {

  /**
   * Constructor
   *
   * @param objectClass the class of this object that already exist
   * @param id          the first element of the id
   * @param ids         the other elements of the id
   */
  public ObjectAlreadyExistingException(Class objectClass, String id, String... ids) {
    super(objectClass, id, ids);
  }

  @Override
  public String getMessage() {
    return "The object "
        + getObjectName()
        + " with the identifiers ("
        + String.join(",", getIds())
        + ") already exist";
  }
}
