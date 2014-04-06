/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wcs.maven.claraxsd;

import com.vaadin.ui.AbstractComponent;
import com.wcs.maven.claraxsd.elementbuilder.ElementBuilderFactory;
import com.wcs.maven.claraxsd.elementbuilder.NopElementBuilder;
import com.wcs.maven.claraxsd.testutils.DumbElementBuilder;
import com.wcs.maven.claraxsd.testutils.XsdTestUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;

/**
 *
 * @author kumm
 */
@RunWith(MockitoJUnitRunner.class)
public class GeneratedSchemaTest {

    private GeneratedSchema instance;
    @Mock
    ElementBuilderFactory elementBuilderFactory;

    @Before
    public void setUp() {
        SchemaLoader schemaLoader = new SchemaLoader();
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
        String expected = "<xs:element name=\"MyFakeComponent\"/>";
        assertEquals(expected, groupAllMarkup);
    }

    @Test
    public void testAppendManagesAllGroup() {
        Mockito.when(elementBuilderFactory.getElementBuilder(MyFakeComponent.class))
                .thenReturn(new DumbElementBuilder());
        instance.append(MyFakeComponent.class);
        String groupAllMarkup = XsdTestUtils.readGeneratedAllComponentsGroupMarkup(instance);
        String expected
                = "<xs:group name=\"AllComponentsGroup\">"
                + "<xs:choice>"
                + "<xs:any namespace=\"##other\"/>"
                + "<xs:element ref=\"a:MyFakeComponent\"/>"
                + "</xs:choice>"
                + "</xs:group>";
        assertEquals(expected, groupAllMarkup);
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

    public static class MyFakeComponent extends AbstractComponent {

    }
    
}
