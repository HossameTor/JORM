package org.jorm.annotations.processors.mapper;

import javax.lang.model.element.VariableElement;

public class FieldTypeMapper {
    public static String mapper(VariableElement f){
        return switch (f.asType().toString()) {
            case "int" -> "INT";
            case "java.lang.String" -> "VARCHAR(255)";
            case "long" -> "BIGINT";
            case "float" -> "FLOAT";
            case "double" -> "DOUBLE";
            case "java.time.LocalDate" -> "DATE";
            case "java.time.LocalDateTime" -> "DATETIME";
            case "boolean" -> "BOOLEAN";
            default -> f.asType().toString().toUpperCase();
        };
    }
}
