package org.jorm.annotations.processors;

import com.google.auto.service.AutoService;
import org.jorm.ConstraintType;
import org.jorm.DatabaseConnection;
import org.jorm.annotations.Constraint;
import org.jorm.annotations.Model;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.List;
import java.util.Queue;
import java.util.Set;
@SupportedAnnotationTypes("org.jorm.annotations.Constraint")
@SupportedSourceVersion(SourceVersion.RELEASE_19)
@AutoService(Processor.class)
public class ConstraintProcessor extends AbstractProcessor {


    /*public boolean process(List<Element> elements) {
        boolean firstElement = true;
        for (Element element : elements) {
            if (element.getAnnotation(Constraint.class)!= null){
                if (element.getKind() == ElementKind.FIELD){
                    if(!firstElement){
                        fieldStatement+= " ,";
                    }else {
                        firstElement = false;
                    }
                    VariableElement fieldElement = (VariableElement) element;
                    fieldStatement += fieldElement.getSimpleName() + " " + FieldTypeMapper(fieldElement);
                    Constraint constraintAnnotation = fieldElement.getAnnotation(Constraint.class);
                    String[] types = constraintAnnotation.types();
                    for(String c : types){
                        fieldStatement += " "+ c;
                    }
                    if (!constraintAnnotation.def().equals("")){
                        fieldStatement += " DEFAULT " + constraintAnnotation.def();
                    }
                }
                fieldStatement +="\n";
            }
        }
        return true;
    }*/
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
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        for (Element element : roundEnv.getElementsAnnotatedWith(Constraint.class)) {
            if (element instanceof VariableElement){
                TypeElement superC =(TypeElement) element.getEnclosingElement();
                String query = "ALTER TABLE " + superC.getSimpleName().toString()+"\n";
                Constraint constraint = element.getAnnotation(Constraint.class);
                List<ConstraintType> types = Arrays.asList(constraint.types());
                if (types.contains(ConstraintType.NotNull)){
                    query+="MODIFY COLUMN "+element.getSimpleName().toString()+" "+ fieldTypeMapper((VariableElement) element)+" NOT NULL";
                    if (types.contains(ConstraintType.UNIQUE)) query+= ",";
                }
                if (types.contains(ConstraintType.UNIQUE)){
                    query+="ADD CONSTRAINT unique_"+element.getSimpleName().toString()+"_constraint UNIQUE ("+
                            element.getSimpleName().toString()+");";
                }
                System.out.println(query);
                alterTable(query);
            }
        }

        return true;
    }
    private void alterTable(String query) {
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
}
