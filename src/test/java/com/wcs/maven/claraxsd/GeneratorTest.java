/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wcs.maven.claraxsd;

import com.wcs.maven.claraxsd.SchemaLoader;
import com.wcs.maven.claraxsd.Generator;
import com.wcs.maven.claraxsd.GeneratedSchema;
import com.wcs.maven.claraxsd.testutils.DumbElementBuilder;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.wcs.maven.claraxsd.elementbuilder.ElementBuilderFactory;
import com.wcs.maven.claraxsd.elementbuilder.NopElementBuilder;
import java.util.Collection;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import static org.mockito.Mockito.*;
import org.mockito.runners.MockitoJUnitRunner;

/**
 *
 * @author kumm
 */
@RunWith(MockitoJUnitRunner.class)
public class GeneratorTest {

    SchemaLoader schemaLoader;
    Generator generator;
    @Mock
    ElementBuilderFactory elementBuilderFactory;
    

    @Before
    public void setUp() {
        schemaLoader = new SchemaLoader();
        generator = new Generator(elementBuilderFactory, schemaLoader);
    }

    @Test
    public void testGenerateCreatesSchemaPerPackage() {
        when(elementBuilderFactory.getElementBuilder(any(Class.class)))
                .thenReturn(new DumbElementBuilder());
        generator.generate(MyFakeComponent.class);
        generator.generate(Label.class);
        generator.generate(VerticalLayout.class);

        Collection<GeneratedSchema> generatedSchemas = generator.getGeneratedSchemas();

        assertEquals(2, generatedSchemas.size());
    }
    
    @Test
    public void testEmptyGeneratedSchemasFitered() {
        when(elementBuilderFactory.getElementBuilder(any(Class.class)))
                .thenReturn(new NopElementBuilder());
        generator.generate(MyFakeComponent.class);
        generator.generate(Label.class);

        Collection<GeneratedSchema> generatedSchemas = generator.getGeneratedSchemas();
        
        assertTrue(generatedSchemas.isEmpty());
    }
    

    public static class MyFakeComponent {
        
    }
}
