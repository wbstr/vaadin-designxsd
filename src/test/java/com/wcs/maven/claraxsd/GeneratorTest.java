/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wcs.maven.claraxsd;

import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.wcs.maven.claraxsd.elementbuilder.ElementBuilderFactory;
import com.wcs.maven.claraxsd.elementbuilder.NopElementBuilder;
import com.wcs.maven.claraxsd.testutils.DumbElementBuilder;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Collection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
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
    

    @Before
    public void setUp() {
        SchemaLoader schemaLoader = new SchemaLoader();
        generator = new Generator(elementBuilderFactory, schemaLoader);
    }

    @Test
    public void testGenerateCreatesSchemaPerPackage() {
        when(elementBuilderFactory.getElementBuilder(Mockito.<Class<? extends Component>>any()))
                .thenReturn(new DumbElementBuilder());
        generator.generate(MyFakeComponent.class);
        generator.generate(Label.class);
        generator.generate(VerticalLayout.class);

        Collection<GeneratedSchema> generatedSchemas = generator.getGeneratedSchemas();

        assertEquals(2, generatedSchemas.size());
    }
    
    @Test
    public void testEmptyGeneratedSchemasFiltered() {
        when(elementBuilderFactory.getElementBuilder(Mockito.<Class<? extends Component>>any()))
                .thenReturn(new NopElementBuilder());
        generator.generate(MyFakeComponent.class);
        generator.generate(Label.class);

        Collection<GeneratedSchema> generatedSchemas = generator.getGeneratedSchemas();
        
        assertTrue(generatedSchemas.isEmpty());
    }
    

    public static class MyFakeComponent extends AbstractComponent {
        
    }
}
