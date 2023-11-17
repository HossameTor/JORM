package org.jorm.mojos;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;

import javax.tools.*;
import java.io.File;
import java.util.Arrays;
import java.util.List;
@Mojo(name = "migrate")
public class MigrateMojo extends AbstractMojo {
    @Override
    public void execute() throws MojoExecutionException {
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, null, null);

        // Set the classpath for the compiler
        List<String> options = Arrays.asList(
                "-classpath", System.getProperty("java.class.path")
        );
        // Set up the annotation processors
        List<String> processorList = Arrays.asList("org.jorm.annotations.processors.ModelProcessor","org.jorm.annotations.processors.QonstraintProcessor");
        options.addAll(Arrays.asList("-processor", String.join(",", processorList)));

        Iterable<? extends JavaFileObject> compilationUnits = fileManager.getJavaFileObjects(new File("src/main/java/YourClass.java"));

        // Invoke the compiler
        JavaCompiler.CompilationTask task = compiler.getTask(
                null, fileManager, null, options, null, compilationUnits
        );

        // Perform the compilation
        if (!task.call()) {
            throw new MojoExecutionException("Annotation processing failed");
        }
    }
}
