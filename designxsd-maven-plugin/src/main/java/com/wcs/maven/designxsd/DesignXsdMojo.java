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
package com.wcs.maven.designxsd;

import com.vaadin.ui.declarative.DesignContext;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;
import org.reflections.Reflections;

@Mojo(name = "generate", defaultPhase = LifecyclePhase.PROCESS_CLASSES,
        requiresDependencyResolution = ResolutionScope.COMPILE)
public class DesignXsdMojo extends AbstractMojo {

    @Component
    private MavenProject mavenProject;

    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    @Parameter(property = "project.compileClasspathElements", required = true, readonly = true)
    private List<String> classpath;

    @Parameter(property = "destination", defaultValue = "${project.build.directory}")
    private String destination;

    @Override
    public void execute()
            throws MojoExecutionException {

        ClassLoader testLoader = oldSetupContextClassLoader();

        Reflections reflections = new Reflections();
        Set<Class<? extends com.vaadin.ui.Component>> allCOmponents = reflections.getSubTypesOf(com.vaadin.ui.Component.class);
        System.out.println("components: " + allCOmponents);

        List<Class<? extends com.vaadin.ui.Component>> filterd = allCOmponents.stream()
                .filter(c -> c.getPackage().getName().startsWith("com.wcs.designxsd.demo"))
                .collect(Collectors.toList());

        System.out.println("filtered: " + filterd);

//        DesignContext designContext = new PackageDiscoverer().discovery(testLoader);
//        System.out.println("prefixes: " + designContext.getPackagePrefixes());

        OutputFilesWriter outputFilesWriter = new OutputFilesWriter(destination);
        generateToVaadinComponents(outputFilesWriter);
//        generateToProjectComponents(outputFilesWriter);
//
//        try {
//            outputFilesWriter.wirteMainXsd();
//        } catch (IOException ex) {
//            throw new MojoExecutionException("Unable to write out files", ex);
//        }
    }

    private ClassLoader oldSetupContextClassLoader() {
        Set<URL> urls = new HashSet<>();
        try {
            for (String element : classpath) {
                urls.add(new File(element).toURI().toURL());
            }
        } catch (MalformedURLException ex) {
            Logger.getLogger(DesignXsdMojo.class.getName()).log(Level.SEVERE, null, ex);
        }

        ClassLoader pluginClassLoader = getClass().getClassLoader();
        ClassLoader mavenClassLoader = pluginClassLoader.getParent();
        ClassLoader testProjectClassLoader
                = new URLClassLoader(urls.toArray(new URL[urls.size()]));

        Thread.currentThread().setContextClassLoader(testProjectClassLoader);
        return testProjectClassLoader;
    }

    private void generateToVaadinComponents(OutputFilesWriter outputFilesWriter) {
        DesignContext designContext = new DesignContext();
        Generator generator = new Generator(new Generator.GeneratedSchemaFactory(designContext));
        GeneratedSchema generatedSchema = generator.generate("com.vaadin.ui");
        outputFilesWriter.appendToMainXsd(generatedSchema);
    }

    private void generateToProjectComponents(OutputFilesWriter outputFilesWriter) {
        PackageDiscoverer packageDiscoverer = new PackageDiscoverer();
//        DesignContext designContext = packageDiscoverer.discovery();
//        for (String packagePrefixe : designContext.getPackagePrefixes()) {
//            String packageName = designContext.getPackage(packagePrefixe);
//            Generator generator = new Generator(new Generator.GeneratedSchemaFactory(designContext));
//            GeneratedSchema generatedSchema = generator.generate(packageName);
//            outputFilesWriter.appendToMainXsd(generatedSchema);
//        }
    }

    private ClassLoader createTestProjectClassLoader() throws MojoExecutionException {
        List<String> testClasspathElements = null;
        try {
            testClasspathElements = this.mavenProject.getTestClasspathElements();
        } catch (DependencyResolutionRequiredException e) {
            new MojoExecutionException("Dependency resolution failed", e);
        }

        List<URL> projectClasspathList = new ArrayList<>();
        for (String element : testClasspathElements) {
            File elementFile = new File(element);
            URL url;
            try {
                url = elementFile.toURI().toURL();
                projectClasspathList.add(url);
            } catch (MalformedURLException ex) {
                throw new MojoExecutionException(element + " is an invalid classpath element", ex);
            }
        }

        ClassLoader pluginClassLoader = getClass().getClassLoader();
        ClassLoader mavenClassLoader = pluginClassLoader.getParent();
        ClassLoader testProjectClassLoader
                = new URLClassLoader(projectClasspathList.toArray(new URL[projectClasspathList.size()]), mavenClassLoader);
        return testProjectClassLoader;
    }
}
