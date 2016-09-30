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
package com.wcs.maven.designxsd.elementbuilder;

import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.declarative.DesignContext;
import com.wcs.maven.designxsd.attributebuilder.AttributeBuilderFactory;
import com.wcs.maven.designxsd.attributebuilder.NopAttributeBuilder;
import com.wcs.maven.designxsd.baseattributegroup.BaseAttributeGroupMngr;
import com.wcs.maven.designxsd.testutils.XsdTestUtils;
import org.apache.ws.commons.schema.XmlSchema;
import org.apache.ws.commons.schema.XmlSchemaCollection;
import org.apache.ws.commons.schema.XmlSchemaElement;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

/**
 *
 * @author kumm
 */
@RunWith(MockitoJUnitRunner.class)
public class ContainerElementBuilderTest {

    private XmlSchema schema;
    private ContainerElementBuilder instance;
    @Mock
    private AttributeBuilderFactory attributeBuilderFactory;
    @Mock
    private BaseAttributeGroupMngr baseAttributeGroupMngr;

    @Before
    public void setUp() {
        when(attributeBuilderFactory.getAttributeBuilder(any(String.class), any(Class.class)))
                .thenReturn(new NopAttributeBuilder());

        instance = new ContainerElementBuilder(attributeBuilderFactory, baseAttributeGroupMngr, new DesignContext());
        schema = new XmlSchema(null, null, new XmlSchemaCollection());
    }

    @Test
    public void testAllComponentsGroupInserted() {
        XmlSchemaElement result = instance.buildElement(schema, VerticalLayout.class);
        String resultMarkup = XsdTestUtils.buildElementMarkup(schema, result);
        String expectedMarkup
                = "<element name=\"vaadin-vertical-layout\">"
                + "<complexType>"
                + "<group maxOccurs=\"unbounded\" minOccurs=\"0\" ref=\"AllComponentsGroup\"/>"
                + "</complexType>"
                + "</element>";

        assertEquals(expectedMarkup, resultMarkup);
    }

}
