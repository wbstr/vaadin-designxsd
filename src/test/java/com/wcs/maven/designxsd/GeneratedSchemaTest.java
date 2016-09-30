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
package com.wcs.maven.designxsd;

import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.Component;
import com.wcs.maven.designxsd.elementbuilder.ElementBuilderFactory;
import com.wcs.maven.designxsd.elementbuilder.NopElementBuilder;
import com.wcs.maven.designxsd.testutils.DumbElementBuilder;
import com.wcs.maven.designxsd.testutils.XsdTestUtils;
import org.apache.ws.commons.schema.XmlSchema;
import org.apache.ws.commons.schema.XmlSchemaInclude;
import org.apache.ws.commons.schema.XmlSchemaObject;
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
        instance = new GeneratedSchema(elementBuilderFactory);
    }

    @Test
    public void testNoTargetNamespace() throws Exception {
        XmlSchema schema = instance.build();
        assertNull(schema.getTargetNamespace());
        assertEquals("xs", schema.getNamespaceContext().getPrefix("http://www.w3.org/2001/XMLSchema"));
        assertEquals(0, schema.getItems().getCount());
    }

    @Test
    public void testIsEmpty() {
        assertTrue(instance.isEmpty());

        Mockito.when(elementBuilderFactory.getElementBuilder(MyFakeComponentA.class))
                .thenReturn(new NopElementBuilder());
        instance.append(MyFakeComponentA.class);
        assertTrue(instance.isEmpty());

        Mockito.when(elementBuilderFactory.getElementBuilder(MyFakeComponentA.class))
                .thenReturn(new DumbElementBuilder());
        instance.append(MyFakeComponentA.class);
        assertFalse(instance.isEmpty());
    }
    
    @Test
    public void testAppendNotSupportedDoesNothing() {
        Mockito.when(elementBuilderFactory.getElementBuilder(MyFakeComponentA.class))
                .thenReturn(new NopElementBuilder());
        instance.append(MyFakeComponentA.class);
        assertTrue(instance.isEmpty());
    }

    @Test
    public void testAppendInsertElements() {
        appendComponents();

        XmlSchema schema = instance.build();
        String elementsMarkup = XsdTestUtils.readGeneratedElementsMarkup(schema);
        String expected
                = "<xs:element name=\"MyFakeComponentA\"/>"
                + "<xs:element name=\"MyFakeComponentB\"/>"
                + "<xs:element name=\"MyFakeComponentC\"/>";
        assertEquals(expected, elementsMarkup);
        schema.write(System.out);
    }

    @Test
    public void testIncludeToMainInsertGroups() throws Exception {
        appendComponents();

        XmlSchema mainXsd = SchemaLoader.load(Generator.class.getResourceAsStream("main_template.xsd"));
        instance.includeToMain(mainXsd, "");
        String markup = XsdTestUtils.readGeneratedAllComponentsGroupMarkup(mainXsd);
        String expected
                = "<xs:group name=\"AllComponentsGroup\">"
                + "<xs:choice>"
                + "<xs:element ref=\"MyFakeComponentA\"/>"
                + "<xs:element ref=\"MyFakeComponentB\"/>"
                + "<xs:element ref=\"MyFakeComponentC\"/>"
                + "</xs:choice>"
                + "</xs:group>";
        assertEquals(expected, markup);
        mainXsd.write(System.out);
    }

    private void appendComponents() {
        Mockito.when(elementBuilderFactory.getElementBuilder(Mockito.<Class<? extends Component>>any()))
                .thenReturn(new DumbElementBuilder());
        instance.append(MyFakeComponentB.class);
        instance.append(MyFakeComponentA.class);
        instance.append(MyFakeComponentC.class);
    }

    @Test
    public void testIncludeToMainInsertInclude() throws Exception {
        XmlSchema mainXsd = SchemaLoader.load(Generator.class.getResourceAsStream("main_template.xsd"));
        instance.includeToMain(mainXsd, "testSchemaLocation.xsd");
        int count = mainXsd.getItems().getCount();
        XmlSchemaObject item = mainXsd.getItems().getItem(count - 1);
        assertTrue(item instanceof XmlSchemaInclude);
        assertEquals("testSchemaLocation.xsd", ((XmlSchemaInclude)item).getSchemaLocation());
        mainXsd.write(System.out);
    }

    public static class MyFakeComponentA extends AbstractComponent {

    }

    public static class MyFakeComponentB extends AbstractComponent {

    }

    public static class MyFakeComponentC extends AbstractComponent {

    }
}
