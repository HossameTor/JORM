package org.jorm.annotations.processors;

import com.google.auto.service.AutoService;
import org.jorm.annotations.Constraint;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import java.util.Set;
@SupportedAnnotationTypes("org.jorm.annotations.Constraint")
@SupportedSourceVersion(SourceVersion.RELEASE_19)
@AutoService(Processor.class)
public class ConstraintProcessor extends AbstractProcessor {
    private String fieldStatement = "(";
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        boolean firstElement = true;
        for (Element element : roundEnv.getElementsAnnotatedWith(Constraint.class)) {
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
            }
            fieldStatement +="\n";
        }
        return true;
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
    public String getFieldsStatement(){
        return fieldStatement;
    }
}
