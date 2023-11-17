package org.jorm.annotations.processors;

import com.google.auto.service.AutoService;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;

import org.jorm.DatabaseConnection;
import org.jorm.annotations.Model;
import org.jorm.annotations.Relationship;
import org.jorm.annotations.processors.mapper.FieldTypeMapper;

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
                String query = createTableQueryBuilder(element);
                System.out.println("-----------------------------------\n"+query);
                createTable(query);
            }
        }
        return true;
    }

    private String createTableQueryBuilder(Element element) {
        TypeElement typeElement = (TypeElement) element;
        StringBuilder query = new StringBuilder("CREATE TABLE IF NOT EXISTS " + typeElement.getSimpleName().toString() + " (");
        List<Element> elements = element.getEnclosedElements().stream().filter((n) -> n.getKind().equals(ElementKind.FIELD)).collect(Collectors.toList());
        boolean firstElement = true;
        for(Element field: elements){
            Annotation relationship = field.getAnnotation(Relationship.class);
            if(relationship!=null){
                continue;
            }
            if(!firstElement) query.append(",");
            else firstElement = false;
            query.append(getFieldStatement((VariableElement) field)).append("\n");
        }
        query.append(");");
        return query.toString();
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

    String getFieldStatement(VariableElement field){
        return field.getSimpleName() + " " + FieldTypeMapper.mapper(field);
    }
}
