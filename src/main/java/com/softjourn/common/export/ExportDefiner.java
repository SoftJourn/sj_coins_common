package com.softjourn.common.export;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ExportDefiner {

    private final String fieldName;

    /**
     * Column name that will be used in report header
     */
    private final String header;

    /**
     * This list is for nested objects
     */
    List<ExportDefiner> definers;

    public ExportDefiner(String fieldName, String header) {
        this.fieldName = fieldName;
        this.header = header;
        definers = new ArrayList<>();
    }

    @Override
    public String toString() {
        return "ExportDefiner{" +
                "fieldName='" + fieldName + '\'' +
                ", header='" + header + '\'' +
                ", definers=" + definers +
                '}';
    }
}
