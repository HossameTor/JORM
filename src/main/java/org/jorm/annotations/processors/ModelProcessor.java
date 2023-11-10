package org.jorm.annotations.processors;

import com.google.auto.service.AutoService;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;

import org.jorm.DatabaseConnection;
import org.jorm.annotations.Model;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

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
                List<VariableElement> fields =(List<VariableElement>) getClassFields(element);
                boolean firstIteration = true;
                for (VariableElement field: fields){
                    if(!firstIteration){
                        query += " , " + getFieldStatement(field) + "\n";
                    }
                    else {
                        query += " " + getFieldStatement(field) + "\n";
                        firstIteration = false;
                    }

                }
                query += ");";
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
    private List<? extends Element> getClassFields(Element element){
        List<? extends Element> fields = element.getEnclosedElements().stream()
                .filter(e -> e.getKind().equals(ElementKind.FIELD)).collect(Collectors.toList());
        return fields;
    }

    private String getFieldStatement(VariableElement e){
        String s = e.getSimpleName() + " " + FieldTypeMapper(e);
        for(AnnotationMirror annotation: e.getAnnotationMirrors()){
            s += " " + getConstraint(annotation);
        }
        return s;
    }

    private String FieldTypeMapper(VariableElement f){
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
    private String getConstraint(AnnotationMirror a){
        switch (a.getAnnotationType().asElement().getSimpleName().toString()){
            case "NotNull":
                return "NOT NULL";
            default:
                return "";
        }
    }
}
