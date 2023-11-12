package org.jorm.annotations.processors;

import com.google.auto.service.AutoService;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;

import org.jorm.DatabaseConnection;
import org.jorm.annotations.Model;
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
                String query = "CREATE TABLE IF NOT EXISTS " + typeElement.getSimpleName().toString() + " ";
                ConstraintProcessor constraintProcessor = new ConstraintProcessor();
                constraintProcessor.init(processingEnv);
                List<Element> elements = element.getEnclosedElements().stream().filter((n) -> n.getKind().equals(ElementKind.FIELD)).collect(Collectors.toList());
                constraintProcessor.process(elements);
                String temp = constraintProcessor.getFieldsStatement();
                query += temp;
                RelationshipProcessor relationshipProcessor = new RelationshipProcessor();
                relationshipProcessor.init(processingEnv);
                relationshipProcessor.process(elements);
                query += relationshipProcessor.getField_stm() + relationshipProcessor.getField_Fk();
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
}
