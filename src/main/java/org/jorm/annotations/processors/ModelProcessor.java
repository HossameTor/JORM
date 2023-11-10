package org.jorm.annotations.processors;

import com.google.auto.service.AutoService;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;

import org.jorm.DatabaseConnection;
import org.jorm.annotations.Model;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Set;

@SupportedAnnotationTypes("org.jorm.annotations.Model")
@SupportedSourceVersion(SourceVersion.RELEASE_19)
@AutoService(Processor.class)
public class ModelProcessor extends AbstractProcessor {
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        for (Element element : roundEnv.getElementsAnnotatedWith(Model.class)) {
            if (element instanceof TypeElement) {
                TypeElement typeElement = (TypeElement) element;
                createTable(typeElement.getSimpleName().toString());
            }
        }
        return true;
    }

    private void createTable(String tableName) {
        DatabaseConnection db = DatabaseConnection.getInstance();
        Connection con = db.getConn();
        Statement statement = null;
        try {
            statement = con.createStatement();
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS " + tableName + " (id INT AUTO_INCREMENT,PRIMARY KEY (id));");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
