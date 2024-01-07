package org.jorm.annotations.processors;

import com.google.auto.service.AutoService;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;

import org.jorm.ConstraintType;
import org.jorm.DatabaseConnection;
import org.jorm.annotations.Constraint;
import org.jorm.annotations.Model;
import org.jorm.annotations.Relationship;
import org.jorm.dto.Class;
import org.jorm.dto.Field;
import org.jorm.helpers.FlexmiGenerator;
import org.jorm.helpers.InputReader;
import org.jorm.transformations.Model2Model;
import org.jorm.transformations.Model2Text;

import java.lang.annotation.Annotation;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
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
                Class c = new Class();
                c.name = typeElement.getSimpleName().toString();
                List<Element> elements = element.getEnclosedElements().stream().filter((n) -> n.getKind().equals(ElementKind.FIELD)).collect(Collectors.toList());
                for(Element field: elements){
                    Relationship relationship = field.getAnnotation(Relationship.class);
                    if(relationship!=null) continue;
                    Field f = new Field();
                    f.name = field.getSimpleName().toString();

                    System.out.println();
                    f.type = field.asType().toString();
                    Constraint constraint = field.getAnnotation(Constraint.class);
                    if(constraint!=null){
                        List<ConstraintType> types = Arrays.asList(constraint.types());
                        if(types.contains(ConstraintType.UNIQUE)) f.uq = true;
                        if(types.contains(ConstraintType.NotNull)) f.nullable = false;
                    }
                    c.fields.add(f);
                }
                Model2Model m = new Model2Model();
                m.setClassFlexmi(FlexmiGenerator.generate(c));
                Model2Text mm = new Model2Text();
                try {
                    mm.m2t(m.pipeLineModel());
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
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
