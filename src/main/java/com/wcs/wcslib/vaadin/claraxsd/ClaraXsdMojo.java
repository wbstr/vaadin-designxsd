package com.wcs.wcslib.vaadin.claraxsd;

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
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.reflections.Reflections;

/**
 * Goal which touches a timestamp file.
 *
 */
@Mojo(name = "trial", defaultPhase = LifecyclePhase.GENERATE_RESOURCES,
        requiresDependencyResolution = ResolutionScope.COMPILE)
public class ClaraXsdMojo
        extends AbstractMojo {

    @Parameter(property = "project.compileClasspathElements", required = true, readonly = true)
    private List<String> classpath;

    @Override
    public void execute()
            throws MojoExecutionException {

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

        Reflections reflections = new Reflections();
        Set<Class<? extends Component>> subTypesOf = reflections.getSubTypesOf(Component.class);

        Generator xmlSchemaHandler = new Generator();

        for (Class<? extends Component> componentClass : subTypesOf) {
            System.out.println("++" + componentClass.getCanonicalName());
            if (Modifier.isAbstract(componentClass.getModifiers())
                    || Modifier.isInterface(componentClass.getModifiers())
                    || !Modifier.isPublic(componentClass.getModifiers())) {
                continue;
            };
            if (componentClass.isMemberClass() && !Modifier.isStatic(componentClass.getModifiers())) {
                continue;
            }
            try {
                Constructor<? extends Component> constructor = componentClass.getConstructor(new Class<?>[]{});
                if (constructor == null || !Modifier.isPublic(constructor.getModifiers())) {
                    continue;
                }
            } catch (NoSuchMethodException e) {
                continue;
            }

            System.out.println("**" + componentClass.getCanonicalName());
//            xmlSchemaHandler.append(componentClass);
        }
        
        
//        xmlSchemaHandler.dump();
    }


}
