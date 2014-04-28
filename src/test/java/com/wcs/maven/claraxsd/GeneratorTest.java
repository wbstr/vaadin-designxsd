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

import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.wcs.maven.claraxsd.elementbuilder.ElementBuilderFactory;
import com.wcs.maven.claraxsd.elementbuilder.NopElementBuilder;
import com.wcs.maven.claraxsd.itest.MyComponent;
import com.wcs.maven.claraxsd.testutils.DumbElementBuilder;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Collection;
import java.util.Iterator;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

/**
 *
 * @author kumm
 */
@RunWith(MockitoJUnitRunner.class)
public class GeneratorTest {

    private Generator generator;
    @Mock
    ElementBuilderFactory elementBuilderFactory;

    private Collection<GeneratedSchema> doGenerate() {
        generator.generate(MyFakeComponent.class);
        generator.generate(Label.class);
        generator.generate(VerticalLayout.class);
        generator.generate(MyComponent.class);
        return generator.getGeneratedSchemas();
    }

    @Before
    public void setUp() {
        generator = new Generator(elementBuilderFactory);
    }

    @Test
    public void testGenerateCreatesSchemaPerPackage() {
        when(elementBuilderFactory.getElementBuilder(Mockito.<Class<? extends Component>>any()))
                .thenReturn(new DumbElementBuilder());
        Collection<GeneratedSchema> generatedSchemas = doGenerate();
        assertEquals(3, generatedSchemas.size());
    }

    @Test
    public void testGeneratedSchemasReturnedAlphabetically() {
        when(elementBuilderFactory.getElementBuilder(Mockito.<Class<? extends Component>>any()))
                .thenReturn(new DumbElementBuilder());
        Iterator<GeneratedSchema> schemaIterator = doGenerate().iterator();
        assertArrayEquals(
                new Package[] {
                        Label.class.getPackage(),
                        MyFakeComponent.class.getPackage(),
                        MyComponent.class.getPackage()
                },
                new Package[] {
                        schemaIterator.next().getComponentPackage(),
                        schemaIterator.next().getComponentPackage(),
                        schemaIterator.next().getComponentPackage(),
                });
    }
    
    @Test
    public void testEmptyGeneratedSchemasFiltered() {
        when(elementBuilderFactory.getElementBuilder(Mockito.<Class<? extends Component>>any()))
                .thenReturn(new NopElementBuilder());
        Collection<GeneratedSchema> generatedSchemas = doGenerate();
        
        assertTrue(generatedSchemas.isEmpty());
    }

    public static class MyFakeComponent extends AbstractComponent {
        
    }

}
