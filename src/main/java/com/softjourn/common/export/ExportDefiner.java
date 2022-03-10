package com.softjourn.common.export;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import lombok.Data;

@Data
public class ExportDefiner {

  /**
   * This list is for nested objects
   */
  List<ExportDefiner> definers;

  /**
   * This list is for nested objects
   */
  Class[] classes;

  /**
   * This list is for parameters
   */
  Object[] parameters;

  /**
   * Name of field or method
   */
  private final String name;

  /**
   * Column name that will be used in report header
   */
  private final String header;

  public ExportDefiner(String name, String header) {
    this.name = name;
    this.header = header;
    definers = new ArrayList<>();
  }

  public ExportDefiner(String name, String header, Class[] classes, Object... parameters) {
    this.name = name;
    this.header = header;
    this.classes = classes;
    this.parameters = parameters;
    definers = new ArrayList<>();
  }

  @Override
  public String toString() {
    return "ExportDefiner{" +
        "name='" + name + '\'' +
        ", header='" + header + '\'' +
        ", definers=" + definers +
        ", classes=" + Arrays.toString(classes) +
        ", parameters=" + Arrays.toString(parameters) +
        '}';
  }
}
