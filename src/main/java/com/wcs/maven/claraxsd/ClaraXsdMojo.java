package com.wcs.maven.claraxsd;

/*
 * Copyright 2001-2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
import com.vaadin.ui.Component;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;

import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.reflections.Reflections;

@Mojo(name = "generate", defaultPhase = LifecyclePhase.GENERATE_RESOURCES,
        requiresDependencyResolution = ResolutionScope.COMPILE)
public class ClaraXsdMojo
        extends AbstractMojo {

    @Parameter(property = "project.compileClasspathElements", required = true, readonly = true)
    private List<String> classpath;

    @Parameter(property = "project.build.directory")
    private String targetDir;

    private Path destinationPath;

    @Override
    public void execute()
            throws MojoExecutionException {

        destinationPath = FileSystems.getDefault().getPath(targetDir, "claraxsd");
        setupContextClassLoader();

        Reflections reflections = new Reflections();
        Set<Class<? extends Component>> allComponentClass = reflections.getSubTypesOf(Component.class);
        Generator generator = Generator.create();
        for (Class<? extends Component> componentClass : allComponentClass) {
            generator.generate(componentClass);
        }

        try {
            writeSchemaFiles(generator);
        } catch (IOException ex) {
            throw new MojoExecutionException("Unable to write out files", ex);
        }
    }

    private void writeSchemaFiles(Generator generator) throws IOException {
        Files.createDirectories(destinationPath);
        copyResource("clara_base.xsd");
        copyResource("clara_parent.xsd");
        Collection<GeneratedSchema> generatedSchemas = generator.getGeneratedSchemas();
        for (GeneratedSchema generatedSchema : generatedSchemas) {
            writeGeneratedSchema(generatedSchema);
        }
    }
    
    private void copyResource(String resource) throws IOException {
        InputStream resourceAsStream = getClass().getResourceAsStream(resource);
        Path destination = destinationPath.resolve(resource);
        Files.copy(resourceAsStream, destination, StandardCopyOption.REPLACE_EXISTING);
    }

    private void writeGeneratedSchema(GeneratedSchema generatedSchema) throws IOException {
        String destfileName = "clara-" + generatedSchema.getComponentPackage().getName() + ".xsd";
        Path destXsdPath = destinationPath.resolve(destfileName);
        Writer writer = new FileWriter(destXsdPath.toFile(), false);
        generatedSchema.write(writer);
    }

    private void setupContextClassLoader() {
        Set<URL> urls = new HashSet<>();
        try {
            for (String element : classpath) {
                urls.add(new File(element).toURI().toURL());
            }
        } catch (MalformedURLException ex) {
            Logger.getLogger(ClaraXsdMojo.class.getName()).log(Level.SEVERE, null, ex);
        }

        ClassLoader contextClassLoader = URLClassLoader.newInstance(
                urls.toArray(new URL[0]),
                Thread.currentThread().getContextClassLoader());
        Thread.currentThread().setContextClassLoader(contextClassLoader);
    }

}