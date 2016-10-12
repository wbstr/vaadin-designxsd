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
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashSet;
import java.util.Set;
import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;

@Mojo(name = "generate", defaultPhase = LifecyclePhase.PROCESS_CLASSES,
        requiresDependencyResolution = ResolutionScope.COMPILE)
public class DesignXsdMojo extends AbstractMojo {

    @Component
    private MavenProject project;

    @Parameter(property = "destination", defaultValue = "${project.build.directory}")
    private String destination;

    @Parameter(property = "projectRootPackage")
    private String projectRootPackage;

    @Override
    public void execute()
            throws MojoExecutionException {

        setupContextClassLoader();
        OutputFilesWriter outputFilesWriter = new OutputFilesWriter(destination);
        generateToVaadinComponents(outputFilesWriter);
        generateToProjectComponents(outputFilesWriter);

        try {
            outputFilesWriter.wirteMainXsd();
        } catch (IOException ex) {
            throw new MojoExecutionException("Unable to write out files", ex);
        }
    }

    private void setupContextClassLoader() throws MojoExecutionException {
        Set<URL> urls = new HashSet<>();
        try {
            // TODO: Lehetne szebben is
            for (String element : project.getCompileClasspathElements()) {
                File file = new File(element);
                if (file.isDirectory()) {
                    listFilesForFolder(urls, file);
                } else {
                    urls.add(new File(element).toURI().toURL());
                }
            }

            ClassLoader contextClassLoader = URLClassLoader.newInstance(
                    urls.toArray(new URL[0]),
                    Thread.currentThread().getContextClassLoader());
            Thread.currentThread().setContextClassLoader(contextClassLoader);
        } catch (MalformedURLException | DependencyResolutionRequiredException ex) {
            throw new MojoExecutionException("Dependency resolution failed", ex);
        }
    }

    private void listFilesForFolder(Set<URL> urls, File folder) throws MalformedURLException {
        for (File fileEntry : folder.listFiles()) {
            if (fileEntry.isDirectory()) {
                listFilesForFolder(urls, fileEntry);
            } else {
                urls.add(fileEntry.toURI().toURL());
            }
        }
    }

    private void generateToVaadinComponents(OutputFilesWriter outputFilesWriter) {
        DesignContext designContext = new DesignContext();
        Generator generator = new Generator(new Generator.GeneratedSchemaFactory(designContext));
        GeneratedSchema generatedSchema = generator.generate("com.vaadin.ui");
        outputFilesWriter.appendToMainXsd(generatedSchema);
    }

    private void generateToProjectComponents(OutputFilesWriter outputFilesWriter) {
        PackageDiscoverer packageDiscoverer = new PackageDiscoverer();
        DesignContext designContext = packageDiscoverer.discovery(projectRootPackage);
        for (String packagePrefixe : designContext.getPackagePrefixes()) {
            String packageName = designContext.getPackage(packagePrefixe);            
            Generator generator = new Generator(new Generator.GeneratedSchemaFactory(designContext));
            GeneratedSchema generatedSchema = generator.generate(packageName);
            outputFilesWriter.appendToMainXsd(generatedSchema);
        }
    }

}
