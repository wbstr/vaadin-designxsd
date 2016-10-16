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

import com.vaadin.ui.declarative.DesignContext;//TODO

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.classworlds.realm.ClassRealm;

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

        setupClassLoader();
        OutputFilesWriter outputFilesWriter = new OutputFilesWriter(destination);
        generateToProjectComponents(outputFilesWriter);

        try {
            outputFilesWriter.wirteMainXsd();
        } catch (IOException ex) {
            throw new MojoExecutionException("Unable to write out files", ex);
        }
    }

    private void setupClassLoader() {
        ClassRealm classLoader = (ClassRealm) getClass().getClassLoader();
        try {
            for (String element : classpath) {
                classLoader.addURL(new File(element).toURI().toURL());
            }
        } catch (MalformedURLException ex) {
            Logger.getLogger(DesignXsdMojo.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void generateToProjectComponents(OutputFilesWriter outputFilesWriter) {
        PackageDiscoverer packageDiscoverer = new PackageDiscoverer();
        DesignContext designContext = packageDiscoverer.discovery();//TODO
        Generator.GeneratedSchemaFactory generatedSchemaFactory = new Generator.GeneratedSchemaFactory(designContext);
        Generator generator = new Generator(generatedSchemaFactory);
        designContext.getPackagePrefixes().stream()
                .filter(packagePrefixe -> !"v".equals(packagePrefixe))
                .forEach(packagePrefixe -> {
                    String packageName = designContext.getPackage(packagePrefixe);
                    GeneratedSchema generatedSchema = generator.generate(packageName);
                    outputFilesWriter.appendToMainXsd(generatedSchema);
                });
    }

}
