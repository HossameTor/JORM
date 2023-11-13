package org.jorm.annotations.processors;

import com.google.auto.service.AutoService;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;

import org.jorm.DatabaseConnection;
import org.jorm.annotations.Model;
import org.jorm.annotations.Relationship;

import java.lang.annotation.Annotation;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@SupportedAnnotationTypes("org.jorm.annotations.Model")
@SupportedSourceVersion(SourceVersion.RELEASE_19)
@AutoService(Processor.class)
public class ModelProcessor extends AbstractProcessor {
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        for (Element element : roundEnv.getElementsAnnotatedWith(Model.class)) {
            if (element instanceof TypeElement) {
                TypeElement typeElement = (TypeElement) element;
                String query = "CREATE TABLE IF NOT EXISTS " + typeElement.getSimpleName().toString() + " (";
                List<Element> elements = element.getEnclosedElements().stream().filter((n) -> n.getKind().equals(ElementKind.FIELD)).collect(Collectors.toList());
                boolean firstElement = true;
                for(Element field: elements){
                    Annotation relationship = field.getAnnotation(Relationship.class);
                    if(relationship!=null){
                        continue;
                    }
                    if(!firstElement) query += ",";
                    else firstElement = false;
                    query += getFieldStatement((VariableElement) field)+"\n";
                }
                query += ");";
                System.out.println(query);
                createTable(query);
            }
        }
        return true;
    }

    private void createTable(String query) {
        DatabaseConnection db = DatabaseConnection.getInstance();
        Connection con = db.getConn();
        Statement statement = null;
        try {
            statement = con.createStatement();
            statement.executeUpdate(query);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private String fieldTypeMapper(VariableElement f){
        switch (f.asType().toString()){
            case "int":
                return "INT";
            case "java.lang.String":
                return "VARCHAR(255)";
            case "long":
                return "BIGINT";
            case  "float":
                return "FLOAT";
            case "double":
                return "DOUBLE";
            case "java.time.LocalDate":
                return "DATE";
            case "java.time.LocalDateTime":
                return "DATETIME";
            case "boolean":
                return "BOOLEAN";
            default:
                return f.asType().toString().toUpperCase();
        }
    }
    String getFieldStatement(VariableElement field){
        String fieldStatement = field.getSimpleName() + " " + fieldTypeMapper(field);
        return fieldStatement;
    }
}
