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

import com.vaadin.ui.Component;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.codehaus.plexus.classworlds.realm.ClassRealm;
import org.reflections.Reflections;

@Mojo(name = "generate", defaultPhase = LifecyclePhase.PROCESS_CLASSES,
        requiresDependencyResolution = ResolutionScope.COMPILE)
public class DesignXsdMojo extends AbstractMojo {

    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    @Parameter(property = "project.compileClasspathElements", required = true, readonly = true)
    private List<String> classpath;

    @Parameter(property = "destination", defaultValue = "${project.build.directory}")
    private String destination;
    
    @Parameter(property = "legacyPrefixEnabled", defaultValue = "false")
    private boolean legacyPrefixEnabled;

    @Override
    public void execute()
            throws MojoExecutionException {

        setupClassLoader();

        Reflections reflections = new Reflections();
        Set<Class<? extends Component>> allComponentClass = reflections.getSubTypesOf(Component.class);
        Generator generator = Generator.create(reflections, legacyPrefixEnabled);
        for (Class<? extends Component> componentClass : allComponentClass) {
            generator.generate(componentClass);
        }

        OutputFilesWriter outputFilesWriter = new OutputFilesWriter(destination);
        Collection<GeneratedSchema> generatedSchemas = generator.getGeneratedSchemas();
        for (GeneratedSchema generatedSchema : generatedSchemas) {
            outputFilesWriter.appendToMainXsd(generatedSchema);
        }

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
}
