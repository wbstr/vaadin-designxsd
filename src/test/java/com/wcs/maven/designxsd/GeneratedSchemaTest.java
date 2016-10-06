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
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;
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

    private GeneratedSchema instance;
    @Mock
    ElementBuilderFactory elementBuilderFactory;

    @Before
    public void setUp() {
        instance = new GeneratedSchema(elementBuilderFactory);
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

        XmlSchema schema = instance.getXmlSchema();

        String elementsMarkup = XsdTestUtils.readGeneratedElementsMarkup(schema, "name=\"");
        String expected
                = "<xs:element name=\"MyFakeComponentA\"/>"
                + "<xs:element name=\"MyFakeComponentB\"/>"
                + "<xs:element name=\"MyFakeComponentC\"/>";
        assertEquals(expected, elementsMarkup);

        String groupMarkup = XsdTestUtils.readGeneratedAllComponentsGroupMarkup(schema);
        expected
                = "<xs:group name=\"AllComponentsGroup\">"
                + "<xs:choice><xs:element ref=\"MyFakeComponentA\"/>"
                + "<xs:element ref=\"MyFakeComponentB\"/>"
                + "<xs:element ref=\"MyFakeComponentC\"/>"
                + "</xs:choice>"
                + "</xs:group>";
        assertEquals(expected, groupMarkup);

        schema.write(System.out);
    }

    private void appendComponents() {
        Mockito.when(elementBuilderFactory.getElementBuilder(Mockito.<Class<? extends Component>>any()))
                .thenReturn(new DumbElementBuilder());
        instance.append(MyFakeComponentB.class);
        instance.append(MyFakeComponentA.class);
        instance.append(MyFakeComponentC.class);
    }

    public static class MyFakeComponentA extends AbstractComponent {

    }

    public static class MyFakeComponentB extends AbstractComponent {

    }

    public static class MyFakeComponentC extends AbstractComponent {

    }
}
