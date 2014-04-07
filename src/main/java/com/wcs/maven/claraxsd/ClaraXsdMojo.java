/*
 * Copyright 2014 Webstar Csoport Kft.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.wcs.maven.claraxsd;

import com.vaadin.ui.Component;
import com.wcs.maven.claraxsd.NamingRules.FixedName;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.reflections.Reflections;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

@Mojo(name = "generate", defaultPhase = LifecyclePhase.GENERATE_RESOURCES,
        requiresDependencyResolution = ResolutionScope.COMPILE)
public class ClaraXsdMojo
        extends AbstractMojo {

    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
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
        for(FixedName fixed: NamingRules.FixedName.values()) {
            copyResource(fixed.getFileName());
        }
        Collection<GeneratedSchema> generatedSchemas = generator.getGeneratedSchemas();
        Collection<Package> packages = new ArrayList<>(generatedSchemas.size());
        for (GeneratedSchema generatedSchema : generatedSchemas) {
            writeGeneratedSchema(generatedSchema);
            packages.add(generatedSchema.getComponentPackage());
        }
        Catalog catalog = new Catalog(packages, destinationPath);
        FileWriter catalogFW = new FileWriter(destinationPath.resolve(NamingRules.CATALOG_FILENAME).toFile());
        catalog.write(catalogFW);
        catalogFW.close();
    }
    
    private void copyResource(String resource) throws IOException {
        InputStream resourceAsStream = getClass().getResourceAsStream(resource);
        Path destination = destinationPath.resolve(resource);
        Files.copy(resourceAsStream, destination, StandardCopyOption.REPLACE_EXISTING);
    }

    private void writeGeneratedSchema(GeneratedSchema generatedSchema) throws IOException {
        String destFileName = NamingRules.getGeneratedXsdFileName(generatedSchema.getComponentPackage());
        Path destXsdPath = destinationPath.resolve(destFileName);
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
                urls.toArray(new URL[urls.size()]),
                Thread.currentThread().getContextClassLoader());
        Thread.currentThread().setContextClassLoader(contextClassLoader);
    }

}
