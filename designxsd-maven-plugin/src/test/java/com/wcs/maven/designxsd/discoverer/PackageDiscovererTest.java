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

import com.vaadin.ui.Window;
import com.vaadin.ui.declarative.Design;
import com.vaadin.ui.declarative.DesignContext;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.reflections.Reflections;

/**
 *
 * @author lali
 */
public class PackageDiscovererTest {

    @Test
    public void testSimpleDiscover() {
        Reflections reflections = new Reflections("com.wcs.maven.designxsd.customcomponent.simple");
        PackageDiscoverer discoverer = new PackageDiscoverer(reflections);
        DesignContext designContext = discoverer.discovery(true);
        Collection<String> packagePrefixes = designContext.getPackagePrefixes();

        Assert.assertTrue(packagePrefixes.contains("custom"));

        String packageName = designContext.getPackage("custom");
        Assert.assertEquals("com.wcs.maven.designxsd.customcomponent.simple", packageName);
    }

    @Test
    public void testNotAmbigous() throws NoSuchFieldException, IllegalArgumentException, IllegalAccessException, Exception {
        Logger logger = Mockito.mock(Logger.class);
        setFinalStatic(PackageDiscoverer.class.getDeclaredField("LOGGER"), logger);

        Reflections reflections = new Reflections("com.wcs.maven.designxsd.customcomponent.notambigous");
        PackageDiscoverer discoverer = new PackageDiscoverer(reflections);
        DesignContext designContext = discoverer.discovery(true);
        Collection<String> packagePrefixes = designContext.getPackagePrefixes();

        Mockito.verify(logger, Mockito.times(1)).log(Mockito.any(Level.class), Mockito.anyString(), Mockito.any(PackageDiscovererException.class));
        Mockito.verify(logger, Mockito.only()).log(Mockito.any(Level.class), Mockito.anyString(), Mockito.any(PackageDiscovererException.class));

        Assert.assertTrue(packagePrefixes.contains("custom"));
    }

    @Test
    public void testSamepackageprefix() throws NoSuchFieldException, IllegalArgumentException, IllegalAccessException, Exception {
        Logger logger = Mockito.mock(Logger.class);
        setFinalStatic(PackageDiscoverer.class.getDeclaredField("LOGGER"), logger);

        Reflections reflections = new Reflections("com.wcs.maven.designxsd.customcomponent.samepackageprefix");
        PackageDiscoverer discoverer = new PackageDiscoverer(reflections);
        DesignContext designContext = discoverer.discovery(true);
        Collection<String> packagePrefixes = designContext.getPackagePrefixes();

        Mockito.verify(logger, Mockito.never()).warning(Mockito.anyString());

        Assert.assertTrue(packagePrefixes.contains("custom"));

        String packageName = designContext.getPackage("custom");
        Assert.assertEquals("com.wcs.maven.designxsd.customcomponent.samepackageprefix", packageName);
    }

    @Test
    public void testlegacyPrefixEnabled() {
        Reflections reflections = new Reflections("com.ui.vaadin");
        PackageDiscoverer discoverer = new PackageDiscoverer(reflections);
        DesignContext designContext = discoverer.discovery(true);

        String tagName = Design.getComponentMapper().componentToTag(new Window(), designContext);
        Assert.assertEquals("v-window", tagName);
    }

    @Test
    public void testlegacyPrefixDisabled() {
        Reflections reflections = new Reflections("com.ui.vaadin");
        PackageDiscoverer discoverer = new PackageDiscoverer(reflections);
        DesignContext designContext = discoverer.discovery(false);

        String tagName = Design.getComponentMapper().componentToTag(new Window(), designContext);
        Assert.assertEquals("vaadin-window", tagName);
    }
    
    @Test
    public void testExceptionHandle() throws Exception {
        Logger logger = Mockito.mock(Logger.class);
        setFinalStatic(PackageDiscoverer.class.getDeclaredField("LOGGER"), logger);
        
        Reflections reflections = new Reflections("com.wcs.maven.designxsd.customcomponent.exception");
        PackageDiscoverer discoverer = new PackageDiscoverer(reflections);
        DesignContext designContext = discoverer.discovery(false);
        
        Assert.assertNotNull(designContext);
    }
    

    static void setFinalStatic(Field field, Object newValue) throws Exception {
        field.setAccessible(true);
        Field modifiersField = Field.class.getDeclaredField("modifiers");
        modifiersField.setAccessible(true);
        modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);
        field.set(null, newValue);
    }
}
