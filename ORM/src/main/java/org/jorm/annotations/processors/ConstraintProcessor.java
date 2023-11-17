package org.jorm.annotations.processors;

import com.google.auto.service.AutoService;
import org.jorm.ConstraintType;
import org.jorm.DatabaseConnection;
import org.jorm.annotations.Constraint;
import org.jorm.annotations.processors.mapper.FieldTypeMapper;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
@SupportedAnnotationTypes("org.jorm.annotations.Constraint")
@SupportedSourceVersion(SourceVersion.RELEASE_19)
@AutoService(Processor.class)
public class ConstraintProcessor extends AbstractProcessor {

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        for (Element element : roundEnv.getElementsAnnotatedWith(Constraint.class)) {
            if (element instanceof VariableElement){
                String query = createConstraintQueryBuilder(element);
                System.out.println(query);
                alterTable(query);
            }
        }

        return true;
    }

    private String createConstraintQueryBuilder(Element element){
        TypeElement superC =(TypeElement) element.getEnclosingElement();
        String query = "ALTER TABLE " + superC.getSimpleName().toString()+"\n";
        Constraint constraint = element.getAnnotation(Constraint.class);
        List<ConstraintType> types = Arrays.asList(constraint.types());
        if (types.contains(ConstraintType.NotNull)){
            query+="MODIFY COLUMN "+element.getSimpleName().toString()+" "+ FieldTypeMapper.mapper((VariableElement) element)+" NOT NULL";
            if (types.contains(ConstraintType.UNIQUE)) query+= ",";
        }
        if (types.contains(ConstraintType.UNIQUE)){
            query+="ADD CONSTRAINT unique_"+element.getSimpleName().toString()+"_constraint UNIQUE ("+
                    element.getSimpleName().toString()+");";
        }
        return query;
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
