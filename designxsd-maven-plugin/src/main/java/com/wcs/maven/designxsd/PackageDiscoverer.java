/*
 * Copyright 2016 lali.
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
package com.wcs.maven.designxsd;

import com.vaadin.annotations.DesignRoot;
import com.vaadin.ui.Component;
import com.vaadin.ui.declarative.Design;
import com.vaadin.ui.declarative.DesignContext;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;
import org.reflections.Reflections;

/**
 *
 * @author lali
 */
public class PackageDiscoverer {

    private static final Logger LOGGER = Logger.getLogger(PackageDiscoverer.class.getName());
    private final static Collection<String> DEFAULT_PREFIXES = new HashSet<String>() {
        {
            add("v");
            add("vaadin");
        }
    };

    private Map<String, String> packages;
    private final String rootPackage;
    private final String extension;

    public PackageDiscoverer(String rootPackage) {
        this.rootPackage = rootPackage;
        this.extension = ".html";
    }

    public PackageDiscoverer(String rootPackage, String extension) {
        this.rootPackage = rootPackage;
        this.extension = extension;
    }

    public Collection<String> discovery() {
        packages = new HashMap<>();
        Set<Class<?>> designRoots = collectDesignRoots();
        for (Class<?> designRoot : designRoots) {
            Component c = newIstance(designRoot);
            if (c != null) {
                collectPrefixes(c);
            }
        }

        return packages.values();
    }

    private void collectPrefixes(Component c) {
        String fileName = c.getClass().getSimpleName() + extension;
        DesignContext designContext = Design.read(fileName, c);
        for (String packagePrefix : designContext.getPackagePrefixes()) {
            if (!DEFAULT_PREFIXES.contains(packagePrefix)) {
                String packageName = designContext.getPackage(packagePrefix);
                putToPackageNames(packagePrefix, packageName);
            }
        }
    }

    private void putToPackageNames(String packagePrefix, String packageName) {
        if (packages.get(packagePrefix) == null) {
            packages.put(packagePrefix, packageName);
        } else {
            packages.remove(packagePrefix);
            LOGGER.warning("Xsd attribute generation skipped. PackagePrefix " + packagePrefix + " is ambigous.");
        }
    }

    private Component newIstance(Class clazz) {
        Constructor constructor = null;
        try {
            constructor = clazz.getConstructor();
        } catch (NoSuchMethodException ex) {
            LOGGER.warning("Xsd attribute generation skipped. No default constructor found in class: " + clazz.getName());
            return null;
        }

        try {
            return (Component) constructor.newInstance();
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            throw new RuntimeException(ex);
        }
    }

    private Set<Class<?>> collectDesignRoots() {
        Reflections reflections = new Reflections(rootPackage);
        return reflections.getTypesAnnotatedWith(DesignRoot.class);
    }
}
