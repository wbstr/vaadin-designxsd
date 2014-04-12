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
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.reflections.Reflections;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

@Mojo(name = "generate", defaultPhase = LifecyclePhase.GENERATE_RESOURCES,
        requiresDependencyResolution = ResolutionScope.COMPILE)
public class ClaraXsdMojo
        extends AbstractMojo {

    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    @Parameter(property = "project.compileClasspathElements", required = true, readonly = true)
    private List<String> classpath;

    @Parameter(property = "baseSystemId", defaultValue = "clara://${project.groupId}:${project.artifactId}:${project.version}/")
    private String baseSystemId;

    @Parameter(property = "destination", defaultValue = "${project.build.directory}/claraxsd")
    private String destination;

    @Override
    public void execute()
            throws MojoExecutionException {

        NamingRules.initBaseSystemIdUri(baseSystemId);
        setupContextClassLoader();

        Reflections reflections = new Reflections();
        Set<Class<? extends Component>> allComponentClass = reflections.getSubTypesOf(Component.class);
        Generator generator = Generator.create();
        for (Class<? extends Component> componentClass : allComponentClass) {
            generator.generate(componentClass);
        }

        OutputFilesWriter outputFilesWriter = new OutputFilesWriter(generator.getGeneratedSchemas(), destination);
        try {
            outputFilesWriter.writeFiles();
        } catch (IOException ex) {
            throw new MojoExecutionException("Unable to write out files", ex);
        }
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
