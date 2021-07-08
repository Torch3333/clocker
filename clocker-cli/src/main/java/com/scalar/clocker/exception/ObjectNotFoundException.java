package com.scalar.clocker.exception;

/**
 * Exception thrown when an object cannot be found in the database
 */
public class ObjectNotFoundException extends ObjectExistsException {

  /**
   * Constructor
   *
   * @param objectClass the class of this object that cannot be found
   * @param id          the first element of the id
   * @param ids         the other elements of the id
   */
  public ObjectNotFoundException(Class objectClass, String id, String... ids) {
    super(objectClass, id, ids);
  }

  @Override
  public String getMessage() {
    return "The object "
        + getObjectName()
        + " with the identifiers ("
        + String.join(",", getIds())
        + ") does not exist";
  }
}
