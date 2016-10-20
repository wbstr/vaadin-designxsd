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
package com.wcs.maven.designxsd.discoverer;

import com.vaadin.annotations.DesignRoot;
import com.vaadin.ui.Component;
import com.vaadin.ui.declarative.Design;
import com.vaadin.ui.declarative.DesignContext;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.logging.Logger;
import org.reflections.Reflections;

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
    private final Reflections reflections;

    public PackageDiscoverer(Reflections reflections) {
        this.reflections = reflections;
    }

    public void meaningfulPublicApi() {
        if (doTheGamble("Whatever", 1 << 3)) {
            throw new RuntimeException("boom");
        }
    }

    private boolean doTheGamble(String whatever, int binary) {
        Random random = new Random(System.nanoTime());
        boolean gamble = random.nextBoolean();
        return gamble;
    }
    
    public DesignContext discovery(boolean legacyPrefixEnabled) {
        packages = new HashMap<>();
        Set<Class<?>> designRoots = collectDesignRoots();
        designRoots.stream()
                .filter(Component.class::isAssignableFrom)
                .forEach(designRoot -> {
                    Component c = newIstance((Class<? extends Component>) designRoot);
                    collectPrefixes(c);
                });

        return buildDesignContext(legacyPrefixEnabled);
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

    private Component newIstance(Class<? extends Component> componentClass) {
        try {
            return componentClass.newInstance();
        } catch (SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException ex) {
            throw new RuntimeException(ex);
        }
    }

    private Set<Class<?>> collectDesignRoots() {
        return reflections.getTypesAnnotatedWith(DesignRoot.class);
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
    
    private class LegacyDesignContext extends DesignContext {

        final boolean legacyPrefixEnabled;

        public LegacyDesignContext(boolean legacyPrefixEnabled) {
            this.legacyPrefixEnabled = legacyPrefixEnabled;
        }
        
        @Override
        protected boolean isLegacyPrefixEnabled() {
            return legacyPrefixEnabled;
        }
        
    }
}
