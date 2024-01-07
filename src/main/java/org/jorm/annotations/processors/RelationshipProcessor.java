package org.jorm.annotations.processors;

import com.google.auto.service.AutoService;
import org.jorm.RelationshipType;
import org.jorm.annotations.Relationship;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import java.util.List;
import java.util.Set;
@SupportedAnnotationTypes("org.jorm.annotations.Relationship")
@SupportedSourceVersion(SourceVersion.RELEASE_19)
public class RelationshipProcessor extends AbstractProcessor {
    private String field_stm="";
    private String field_Fk="";
    public void process(List<Element> elements){
        for(Element element : elements){
            if(element.getAnnotation(Relationship.class)!= null){
                if (element.getKind() == ElementKind.FIELD){
                    VariableElement fieldElement = (VariableElement) element;
                    Relationship relationshipAnnotation = fieldElement.getAnnotation(Relationship.class);
                    if(relationshipAnnotation.type() == RelationshipType.One_To_One){
                        String[] fullName = element.asType().toString().split("[.]");
                        String name = fullName[fullName.length-1];
                        field_stm = ", " +relationshipAnnotation.joinColumn()[0]+ " INT UNIQUE"+"\n";
                        field_Fk = ", FOREIGN KEY ("+ relationshipAnnotation.joinColumn()[0]+") REFERENCES "+
                                name.toUpperCase()+" ("+relationshipAnnotation.joinColumn()[1]+")\n";
                    }
                }
            }

        }
    }
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {

        return false;
    }

    public String getField_Fk() {
        return field_Fk;
    }

    public String getField_stm() {
        return field_stm;
    }
}
