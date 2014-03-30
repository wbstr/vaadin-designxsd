/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wcs.maven.claraxsd;

import com.wcs.maven.claraxsd.SchemaLoader;
import com.wcs.maven.claraxsd.GeneratedSchema;
import com.wcs.maven.claraxsd.testutils.XsdTestUtils;
import com.wcs.maven.claraxsd.testutils.DumbElementBuilder;
import com.wcs.maven.claraxsd.elementbuilder.ElementBuilder;
import com.wcs.maven.claraxsd.elementbuilder.ElementBuilderFactory;
import com.wcs.maven.claraxsd.elementbuilder.NopElementBuilder;
import org.apache.ws.commons.schema.XmlSchema;
import org.apache.ws.commons.schema.XmlSchemaElement;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

/**
 *
 * @author kumm
 */
@RunWith(MockitoJUnitRunner.class)
public class GeneratedSchemaTest {

    GeneratedSchema instance;
    SchemaLoader schemaLoader;
    @Mock
    ElementBuilderFactory elementBuilderFactory;

    @Before
    public void setUp() {
        schemaLoader = new SchemaLoader();
        instance = new GeneratedSchema(getClass().getPackage(), schemaLoader, elementBuilderFactory);
    }

    @Test
    public void testGetComponentPackage() {
        assertEquals(getClass().getPackage(), instance.getComponentPackage());
    }

    @Test
    public void testIsEmpty() {
        assertTrue(instance.isEmpty());

        Mockito.when(elementBuilderFactory.getElementBuilder(MyFakeComponent.class))
                .thenReturn(new NopElementBuilder());
        instance.append(MyFakeComponent.class);
        assertTrue(instance.isEmpty());

        Mockito.when(elementBuilderFactory.getElementBuilder(MyFakeComponent.class))
                .thenReturn(new DumbElementBuilder());
        instance.append(MyFakeComponent.class);
        assertFalse(instance.isEmpty());
    }
    
    @Test
    public void testEmptyAllComponentsGroupLoaded() {
        assertAllGroupIsEmpty();
    }

    @Test
    public void testAppendNotSupportedDoesNothing() {
        Mockito.when(elementBuilderFactory.getElementBuilder(MyFakeComponent.class))
                .thenReturn(new NopElementBuilder());
        instance.append(MyFakeComponent.class);
        assertAllGroupIsEmpty();
    }

    @Test
    public void testAppendInsertsElement() {
        Mockito.when(elementBuilderFactory.getElementBuilder(MyFakeComponent.class))
                .thenReturn(new DumbElementBuilder());
        instance.append(MyFakeComponent.class);
        String groupAllMarkup = XsdTestUtils.readGeneratedElementsMarkup(instance);
        String expexted = "<xs:element name=\"MyFakeComponent\"/>";
        assertEquals(expexted, groupAllMarkup);
    }

    @Test
    public void testAppendManagesAllGroup() {
        Mockito.when(elementBuilderFactory.getElementBuilder(MyFakeComponent.class))
                .thenReturn(new DumbElementBuilder());
        instance.append(MyFakeComponent.class);
        String groupAllMarkup = XsdTestUtils.readGeneratedAllComponentsGroupMarkup(instance);
        String expexted
                = "<xs:group name=\"AllComponentsGroup\">"
                + "<xs:choice>"
                + "<xs:any namespace=\"##other\"/>"
                + "<xs:element ref=\"a:MyFakeComponent\"/>"
                + "</xs:choice>"
                + "</xs:group>";
        assertEquals(expexted, groupAllMarkup);
    }

    private void assertAllGroupIsEmpty() {
        String expected 
            = "<xs:group name=\"AllComponentsGroup\">"
            + "<xs:choice>"
            + "<xs:any namespace=\"##other\"/>"
            + "</xs:choice>"
            + "</xs:group>";
        assertEquals(expected, XsdTestUtils.readGeneratedAllComponentsGroupMarkup(instance));
    }

    public static class MyFakeComponent {

    }
    
}
