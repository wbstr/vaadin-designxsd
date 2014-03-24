/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wcs.wcslib.vaadin.claraxsd;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import javax.xml.namespace.QName;
import javax.xml.transform.OutputKeys;
import org.apache.ws.commons.schema.XmlSchemaAttribute;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author kumm
 */
public class GeneratedSchemaTest {

    private static Map writeOptions = new HashMap() {
        {
            put(OutputKeys.INDENT, "no");
        }
    };

    GeneratedSchema generatedSchema;

    @Before
    public void setUp() {
        SchemaGenerator schemaGenerator = new SchemaGenerator();
        generatedSchema = new GeneratedSchema(schemaGenerator, getClass().getPackage());
    }

    @Test
    public void testAppendComponent() {
        generatedSchema.append(MyComponent.class);
        String generatedElementMarkup = readGeneratedElementsMarkup(generatedSchema);

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
        String markup = readMarkup(generatedSchema);
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
        String generatedElementMarkup = readGeneratedElementsMarkup(generatedSchema);

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
        String generatedElementMarkup = readGeneratedElementsMarkup(generatedSchema);

        String expectedMarkup
                = "<xs:element name=\"MySingleComponentContainer\">"
                + "<xs:complexType>"
                + "<xs:group minOccurs=\"0\" ref=\"a:AllComponentsGroup\"/>"
                + "<xs:attributeGroup ref=\"b:com.vaadin.ui.AbstractComponent\"/>"
                + "</xs:complexType>"
                + "</xs:element>";

        assertEquals(expectedMarkup, generatedElementMarkup);
    }

    @Test
    public void testAttributesAppended() {
        SchemaGenerator schemaGenerator = new SchemaGenerator(
                SchemaGenerator.class.getResourceAsStream("clara_base.xsd"),
                new ArrayList<AttributeGenerator>() {
                    {
                        add(new AttributeGenerator() {

                            @Override
                            public XmlSchemaAttribute generate(String name, Class<?> type) {
                                switch (name) {
                                    case "aProp":
                                    case "bProp":
                                    case "cProp":
                                        XmlSchemaAttribute attr = new XmlSchemaAttribute();
                                        attr.setName(name);
                                        return attr;
                                }
                                return null;
                            }

                            @Override
                            public boolean isSupports(Class<?> type) {
                                return true;
                            }
                        });
                    }
                }
        );
        generatedSchema = new GeneratedSchema(schemaGenerator, getClass().getPackage());

        generatedSchema.append(MyComponent.class);
        String generatedElementMarkup = readGeneratedElementsMarkup(generatedSchema);

        String expectedMarkup
                = "<xs:element name=\"MyComponent\">"
                + "<xs:complexType>"
                + "<xs:attributeGroup ref=\"b:com.vaadin.ui.AbstractComponent\"/>"
                + "<xs:attribute name=\"aProp\"/>"
                + "<xs:attribute name=\"bProp\"/>"
                + "<xs:attribute name=\"cProp\"/>"
                + "</xs:complexType>"
                + "</xs:element>";

        assertEquals(expectedMarkup, generatedElementMarkup);
    }

    private String readMarkup(GeneratedSchema generatedSchema) {
        StringWriter markupWriter = new StringWriter();
        generatedSchema.write(markupWriter, writeOptions);
        return markupWriter.toString();
    }

    private String readGeneratedElementsMarkup(GeneratedSchema generatedSchema) {
        String markup = readMarkup(generatedSchema);
        int beginIndex = markup.indexOf("</xs:group>") + 11;
        int endIndex = markup.indexOf("</xs:schema>");
        return markup.substring(beginIndex, endIndex);
    }

}
