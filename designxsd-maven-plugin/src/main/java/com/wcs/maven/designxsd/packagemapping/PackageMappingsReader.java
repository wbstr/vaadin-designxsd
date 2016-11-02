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
package com.wcs.maven.designxsd.packagemapping;

import com.vaadin.annotations.DesignRoot;
import com.vaadin.ui.declarative.DesignContext;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.reflections.Reflections;

/**
 * @author lali
 */
public class PackageMappingsReader {

    private static final Logger LOGGER = Logger.getLogger(PackageMappingsReader.class.getName());
    private final static Collection<String> DEFAULT_PREFIXES = new HashSet<String>() {
        {
            add("v");
            add("vaadin");
        }
    };

    private Map<String, String> packages;
    private final Reflections reflections;

    public PackageMappingsReader(Reflections reflections) {
        this.reflections = reflections;
    }

    public DesignContext discovery(boolean legacyPrefixEnabled) {
        packages = new HashMap<>();
        Set<Class<?>> designRoots = reflections.getTypesAnnotatedWith(DesignRoot.class);
        for (Class<?> annotatedClass : designRoots) {
            try {
                InputStream designFile = readDesignFile(annotatedClass);
                DesignContext designContext = new PackageDesignContext(designFile);
                collectPrefixes(designContext);
            } catch (PackageMappingsReaderException ex) {
                LOGGER.log(Level.WARNING,
                        "Package discover skipped. Component name: "
                        + annotatedClass.getName(),
                        ex);
            }
        }

        return buildDesignContext(legacyPrefixEnabled);
    }

    private InputStream readDesignFile(Class<?> annotatedClass) throws PackageMappingsReaderException {
        DesignRoot designAnnotation = annotatedClass
                .getAnnotation(DesignRoot.class);
        String filename = designAnnotation.value();
        if (filename.equals("")) {
            // No value, assume the html file is named as the class
            filename = annotatedClass.getSimpleName() + ".html";
        }

        InputStream stream = annotatedClass.getResourceAsStream(filename);
        if (stream == null) {
            throw new PackageMappingsReaderException("Unable to find design file " + filename
                    + " in " + annotatedClass.getPackage().getName());
        }

        return stream;
    }

    private void collectPrefixes(DesignContext designContext) throws PackageMappingsReaderException {
        for (String packagePrefix : designContext.getPackagePrefixes()) {
            if (!DEFAULT_PREFIXES.contains(packagePrefix)) {
                String packageName = designContext.getPackage(packagePrefix);
                putToPackageNames(packagePrefix, packageName);
            }
        }
    }

    private void putToPackageNames(String packagePrefix, String packageName) throws PackageMappingsReaderException {
        String processedPackageName = packages.get(packagePrefix);
        if (processedPackageName != null && !processedPackageName.equals(packageName)) {
            throw new PackageMappingsReaderException(
                    "Xsd attribute generation skipped. PackagePrefix "
                    + packagePrefix
                    + " is ambigous.");
        } else {
            packages.put(packagePrefix, packageName);
        }
    }

    private DesignContext buildDesignContext(boolean legacyPrefixEnabled) {
        DesignContext designContext = new LegacyDesignContext(legacyPrefixEnabled);
        for (Map.Entry<String, String> entry : packages.entrySet()) {
            String packagePrefix = entry.getKey();
            String packageName = entry.getValue();

            designContext.addPackagePrefix(packagePrefix, packageName);
        }
        return designContext;

    }

}
