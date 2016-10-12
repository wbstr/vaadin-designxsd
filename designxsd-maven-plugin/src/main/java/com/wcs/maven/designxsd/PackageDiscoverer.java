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
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Collectors;
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

    public DesignContext discovery(String rootPackage) {
        packages = new HashMap<>();
        List<Class<Component>> designRoots = collectDesignRoots(rootPackage);
        for (Class<Component> designRoot : designRoots) {
            Component c = newIstance(designRoot);
            if (c != null) {
                collectPrefixes(c);
            }
        }

        return buildDesignContext();
    }

    private void collectPrefixes(Component c) {
        DesignContext designContext = Design.read(c);
        for (String packagePrefix : designContext.getPackagePrefixes()) {
            if (!DEFAULT_PREFIXES.contains(packagePrefix)) {
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

    private Component newIstance(Class clazz) {
        try {
            Constructor constructor = clazz.getConstructor();
            return (Component) constructor.newInstance();
        } catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            throw new RuntimeException(ex);
        }
    }

    private List<Class<Component>> collectDesignRoots(String rootPackage) {
        System.out.println("Collect design files from " + rootPackage + " and " + Component.class.getPackage().getName());

        Reflections reflections = new Reflections(Component.class.getPackage().getName(), rootPackage);
        Set<Class<?>> annotatedWith = reflections.getTypesAnnotatedWith(DesignRoot.class);

        System.out.println("Found:" + annotatedWith);

        List<Class<Component>> resutl = annotatedWith.stream()
                .filter(c -> c.getPackage().getName().startsWith(rootPackage))
                .map(c -> (Class<Component>) c)
                .collect(Collectors.toList());

        System.out.println("Result: " + resutl);
        return resutl;
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
