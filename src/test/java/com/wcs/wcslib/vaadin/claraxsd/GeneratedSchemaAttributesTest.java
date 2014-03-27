/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wcs.wcslib.vaadin.claraxsd;

import com.wcs.wcslib.vaadin.claraxsd.attributeproducer.AttributeProducer;
import java.util.ArrayList;
import org.apache.ws.commons.schema.XmlSchema;
import org.apache.ws.commons.schema.XmlSchemaAttribute;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author kumm
 */
public class GeneratedSchemaAttributesTest {

    GeneratedSchema generatedSchema;

    AttributeProducer skipObjectAttributeProducer = new AttributeProducer() {

        @Override
        public XmlSchemaAttribute produce(XmlSchema schema, String name, Class<?> type) {
            XmlSchemaAttribute attr = new XmlSchemaAttribute();
            attr.setName(name);
            return attr;
        }

        @Override
        public boolean isSupports(Class<?> type) {
            return type != Object.class;
        }
    };

    @Before
    public void setUp() {
        Generator schemaGenerator = new Generator(
                GeneratedSchemaAttributesTest.class.getResourceAsStream("clara_base_attributes_test.xsd"),
                new ArrayList<AttributeProducer>() {{
                    add(skipObjectAttributeProducer);
                }}
        );
        generatedSchema = new GeneratedSchema(schemaGenerator, getClass().getPackage());
    }

    @Test
    public void testSupportedAttributesAppendedAlphabetically() {
        generatedSchema.append(AttributeTestSubject.class);
        String generatedElementMarkup = XsdTestUtils.readGeneratedAttributesMarkup(generatedSchema);

        String expectedMarkup
                = "<xs:attributeGroup ref=\"b:com.wcs.wcslib.vaadin.claraxsd.AttributeTestSubject\"/>"
                + "<xs:attribute name=\"aStringProp\"/>"
                + "<xs:attribute name=\"bIntProp\"/>"
                + "<xs:attribute name=\"cVoidProp\"/>";

        assertEquals(expectedMarkup, generatedElementMarkup);
    }

    @Test
    public void testAttributesInGroupSkipped() {
        generatedSchema.append(AttributeTestSubjectExtended.class);
        String generatedElementMarkup = XsdTestUtils.readGeneratedAttributesMarkup(generatedSchema);

        String expectedMarkup
                = "<xs:attributeGroup ref=\"b:com.wcs.wcslib.vaadin.claraxsd.AttributeTestSubject\"/>"
                + "<xs:attribute name=\"aStringProp\"/>"
                + "<xs:attribute name=\"bIntProp\"/>"
                + "<xs:attribute name=\"cVoidProp\"/>"
                + "<xs:attribute name=\"dprop\"/>";

        assertEquals(expectedMarkup, generatedElementMarkup);
    }

}
