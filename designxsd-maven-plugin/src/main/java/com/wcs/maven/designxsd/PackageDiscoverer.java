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

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;

/**
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

    public DesignContext discovery() {
        packages = new HashMap<>();
        Set<Class<?>> designRoots = collectDesignRoots();
        designRoots.stream()
                .filter(Component.class::isAssignableFrom)
                .forEach(designRoot -> {
                    Component c = newIstance((Class<? extends Component>) designRoot);
                    collectPrefixes(c);
                });

        return buildDesignContext();
    }

    private void collectPrefixes(Component c) {
        System.out.println("Call by2: " + c.getClass().getName());
        DesignContext designContext = Design.read(c);
        for (String packagePrefix : designContext.getPackagePrefixes()) {
            if (!DEFAULT_PREFIXES.contains(packagePrefix)) {
                System.out.println("Discover: " + packagePrefix);
                String packageName = designContext.getPackage(packagePrefix);
                putToPackageNames(packagePrefix, packageName);
            }
        }
    }

    private void putToPackageNames(String packagePrefix, String packageName) {
        String processedPackageName = packages.get(packagePrefix);
        if (processedPackageName != null && !processedPackageName.equals(packageName)) {
            LOGGER.warning("Xsd attribute generation skipped. PackagePrefix " + packagePrefix + " is ambigous.");
        } else {
            packages.put(packagePrefix, packageName);
        }
    }

    private Component newIstance(Class<? extends Component> componentClass) {
        try {
            return componentClass.newInstance();
        } catch (SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException ex) {
            throw new RuntimeException(ex);
        }
    }

    private Set<Class<?>> collectDesignRoots() {
        Reflections reflections = new Reflections(new SubTypesScanner(), new TypeAnnotationsScanner());
        return reflections.getTypesAnnotatedWith(DesignRoot.class);
    }

    private DesignContext buildDesignContext() {
        DesignContext designContext = new DesignContext();
        for (Map.Entry<String, String> entry : packages.entrySet()) {
            String packagePrefix = entry.getKey();
            String packageName = entry.getValue();

            designContext.addPackagePrefix(packagePrefix, packageName);
        }
        return designContext;
    }
}
