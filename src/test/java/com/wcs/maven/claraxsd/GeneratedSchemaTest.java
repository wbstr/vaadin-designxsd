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
        instance = new GeneratedSchema(getClass().getPackage(), elementBuilderFactory);
    }

    @Test
    public void testGetComponentPackage() {
        assertEquals(getClass().getPackage(), instance.getComponentPackage());
    }

    @Test
    public void testTargetNamespace() {
        String markup = XsdTestUtils.readMarkup(instance);
        int beginIndex = markup.indexOf("<schema") + "<schema".length();
        int endIndex = markup.indexOf(">", beginIndex);
        assertTrue(beginIndex > -1);
        assertTrue(endIndex > beginIndex);
        String schemaElementStr = markup.substring(beginIndex, endIndex);
        assertTrue(schemaElementStr.contains(" targetNamespace=\"urn:import:" + getClass().getPackage().getName() + "\""));
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
    public void testEmptyAllComponentsGroupLoaded() {
        assertAllGroupIsEmpty();
    }

    @Test
    public void testAppendNotSupportedDoesNothing() {
        Mockito.when(elementBuilderFactory.getElementBuilder(MyFakeComponentA.class))
                .thenReturn(new NopElementBuilder());
        instance.append(MyFakeComponentA.class);
        assertAllGroupIsEmpty();
        assertTrue(instance.isEmpty());
    }

    @Test
    public void testAppendInsertElements() {
        Mockito.when(elementBuilderFactory.getElementBuilder(Mockito.<Class<? extends Component>>any()))
                .thenReturn(new DumbElementBuilder());
        instance.append(MyFakeComponentB.class);
        instance.append(MyFakeComponentA.class);
        instance.append(MyFakeComponentC.class);
        String groupAllMarkup = XsdTestUtils.readGeneratedElementsMarkup(instance);
        String expected
                = "<element name=\"MyFakeComponentA\"/>"
                + "<element name=\"MyFakeComponentB\"/>"
                + "<element name=\"MyFakeComponentC\"/>";
        assertEquals(expected, groupAllMarkup);
    }

    @Test
    public void testAppendManagesAllGroup() {
        Mockito.when(elementBuilderFactory.getElementBuilder(Mockito.<Class<? extends Component>>any()))
                .thenReturn(new DumbElementBuilder());
        instance.append(MyFakeComponentB.class);
        instance.append(MyFakeComponentA.class);
        instance.append(MyFakeComponentC.class);
        String groupAllMarkup = XsdTestUtils.readGeneratedAllComponentsGroupMarkup(instance);
        String expected
                = "<group name=\"AllComponentsGroup\">"
                + "<choice>"
                + "<any namespace=\"##other\"/>"
                + "<element ref=\"tns:MyFakeComponentA\"/>"
                + "<element ref=\"tns:MyFakeComponentB\"/>"
                + "<element ref=\"tns:MyFakeComponentC\"/>"
                + "</choice>"
                + "</group>";
        assertEquals(expected, groupAllMarkup);
    }

    private void assertAllGroupIsEmpty() {
        String expected 
            = "<group name=\"AllComponentsGroup\">"
            + "<choice>"
            + "<any namespace=\"##other\"/>"
            + "</choice>"
            + "</group>";
        assertEquals(expected, XsdTestUtils.readGeneratedAllComponentsGroupMarkup(instance));
    }

    public static class MyFakeComponentA extends AbstractComponent {

    }

    public static class MyFakeComponentB extends AbstractComponent {

    }

    public static class MyFakeComponentC extends AbstractComponent {

    }
}
