package org.jorm.transformations;

import org.jorm.helpers.FileReader;
import org.jorm.helpers.ModelLoader;
import org.eclipse.epsilon.emc.emf.InMemoryEmfModel;
import org.eclipse.epsilon.etl.EtlModule;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

public class Model2Model {
    //private String pipeline = FileReader.readFile("demo/src/main/resources/pipeline.emf");
    ///private String  input = FileReader.readFile(pipeline);
    //private String transformation = FileReader.readFile("demo/src/main/resources/input2pipeline.etl");
    private String classFlexmi;

    public InMemoryEmfModel pipeLineModel() throws Exception{
        System.out.println("modeltomodeltr");
        EtlModule module = new EtlModule();
        String resourcePath = "/m2m.etl";
        module.parse(new FileReader().readFile(resourcePath));
        if(!module.getParseProblems().isEmpty()){
            throw new RuntimeException(module.getParseProblems().get(0).toString());
        }
        module.getContext().setOutputStream(System.out);
        return runTransformation(
                module
        );
    }

    public String getClassFlexmi() {
        return classFlexmi;
    }

    public void setClassFlexmi(String classFlexmi) {
        this.classFlexmi = classFlexmi;
    }

    private InMemoryEmfModel runTransformation(EtlModule module)
            throws Exception {
        System.out.println("runTransformation");
        InMemoryEmfModel classModel = ModelLoader.getInMemoryFlexmiModel(classFlexmi,new FileReader().readFile("/class.emf") );
        classModel.setName("Source");
        InMemoryEmfModel tableModel = ModelLoader.getBlankInMemoryModel(new FileReader().readFile("/table.emf"));
        tableModel.setName("Target");
        module.getContext().getModelRepository().addModel(classModel);
        module.getContext().getModelRepository().addModel(tableModel);
        module.execute();

        return tableModel;
    }
}
