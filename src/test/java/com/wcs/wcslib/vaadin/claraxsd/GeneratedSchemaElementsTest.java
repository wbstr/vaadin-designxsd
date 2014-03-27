/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wcs.wcslib.vaadin.claraxsd;

import java.util.Collections;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author kumm
 */
public class GeneratedSchemaElementsTest {

    GeneratedSchema generatedSchema;

    @Before
    public void setUp() {
        Generator schemaGenerator = new Generator(
                Generator.class.getResourceAsStream("clara_base.xsd"),
                Collections.EMPTY_LIST
        );
        generatedSchema = new GeneratedSchema(schemaGenerator, getClass().getPackage());
    }

    @Test
    public void testAppendComponent() {
        generatedSchema.append(MyComponent.class);
        String generatedElementMarkup = XsdTestUtils.readGeneratedElementsMarkup(generatedSchema);

        String expectedMarkup
                = "<xs:element name=\"MyComponent\">"
                + "<xs:complexType>"
                + "<xs:attributeGroup ref=\"b:com.vaadin.ui.AbstractComponent\"/>"
                + "</xs:complexType>"
                + "</xs:element>";

        assertEquals(expectedMarkup, generatedElementMarkup);
    }

    @Test
    public void testAppendManagesAllGroup() {
        generatedSchema.append(MyComponent.class);
        String markup = XsdTestUtils.readMarkup(generatedSchema);
        int beginIndex = markup.indexOf("<xs:group ");
        int endIndex = markup.indexOf("</xs:group>") + 11;
        String groupAllMarkup = markup.substring(beginIndex, endIndex);
        String expexted
                = "<xs:group name=\"AllComponentsGroup\">"
                + "<xs:choice>"
                + "<xs:any namespace=\"##other\"/>"
                + "<xs:element ref=\"a:MyComponent\"/>"
                + "</xs:choice>"
                + "</xs:group>";
        assertEquals(expexted, groupAllMarkup);
    }

    @Test
    public void testAppendContainer() {
        generatedSchema.append(MyComponentContainer.class);
        String generatedElementMarkup = XsdTestUtils.readGeneratedElementsMarkup(generatedSchema);

        String expectedMarkup
                = "<xs:element name=\"MyComponentContainer\">"
                + "<xs:complexType>"
                + "<xs:group maxOccurs=\"unbounded\" minOccurs=\"0\" ref=\"a:AllComponentsGroup\"/>"
                + "<xs:attributeGroup ref=\"b:com.vaadin.ui.AbstractComponent\"/>"
                + "</xs:complexType>"
                + "</xs:element>";

        assertEquals(expectedMarkup, generatedElementMarkup);
    }

    @Test
    public void testAppendSingleContainer() {
        generatedSchema.append(MySingleComponentContainer.class);
        String generatedElementMarkup = XsdTestUtils.readGeneratedElementsMarkup(generatedSchema);

        String expectedMarkup
                = "<xs:element name=\"MySingleComponentContainer\">"
                + "<xs:complexType>"
                + "<xs:group minOccurs=\"0\" ref=\"a:AllComponentsGroup\"/>"
                + "<xs:attributeGroup ref=\"b:com.vaadin.ui.AbstractComponent\"/>"
                + "</xs:complexType>"
                + "</xs:element>";

        assertEquals(expectedMarkup, generatedElementMarkup);
    }

}
