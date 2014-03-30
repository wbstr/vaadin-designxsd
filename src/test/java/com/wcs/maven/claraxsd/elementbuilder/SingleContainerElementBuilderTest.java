/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wcs.maven.claraxsd.elementbuilder;

import com.wcs.maven.claraxsd.elementbuilder.SingleContainerElementBuilder;
import com.wcs.maven.claraxsd.testutils.XsdTestUtils;
import com.wcs.maven.claraxsd.attributebuilder.AttributeBuilderFactory;
import com.wcs.maven.claraxsd.baseattributegroup.BaseAttributeGroupMngr;
import org.apache.ws.commons.schema.XmlSchema;
import org.apache.ws.commons.schema.XmlSchemaCollection;
import org.apache.ws.commons.schema.XmlSchemaElement;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

/**
 *
 * @author kumm
 */
@RunWith(MockitoJUnitRunner.class)
public class SingleContainerElementBuilderTest {

    private XmlSchema schema;
    private SingleContainerElementBuilder instance;
    @Mock
    private AttributeBuilderFactory attributeBuilderFactory;
    @Mock
    private BaseAttributeGroupMngr baseAttributeGroupMngr;

    @Before
    public void setUp() {
        instance = new SingleContainerElementBuilder(attributeBuilderFactory, baseAttributeGroupMngr);
        schema = new XmlSchema(null, null, new XmlSchemaCollection());
    }

    @Test
    public void testAllComponentsGroupInserted() {
        XmlSchemaElement result = instance.buildElement(schema, MyFakeComponent.class);
        String resultMarkup = XsdTestUtils.buildElementMarkup(schema, result);
        String expectedMarkup
                = "<element name=\"MyFakeComponent\">"
                + "<complexType>"
                + "<group minOccurs=\"0\" ref=\"AllComponentsGroup\"/>"
                + "</complexType>"
                + "</element>";

        assertEquals(expectedMarkup, resultMarkup);
    }

    public static class MyFakeComponent {
    }
}
