package com.scalar.clocker.repository;

import com.scalar.db.api.Result;
import com.scalar.db.io.TextValue;
import java.util.function.Consumer;

public class Util {

  /**
   * Retrieve the optional value for the columnName from the result. If the value is present, set it
   * to the setter method.
   *
   * @param result     the result
   * @param columnName the column name
   * @param setter     the method reference for a setter of a text attribute
   */
  public static void setTextValueIfPresent(
      Result result, String columnName, Consumer<? super String> setter) {
    result.getValue(columnName).flatMap(value -> ((TextValue) value).getString()).ifPresent(setter);
  }
}
