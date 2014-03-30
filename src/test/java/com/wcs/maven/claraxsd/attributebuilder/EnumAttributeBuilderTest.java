/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.wcs.maven.claraxsd.attributebuilder;

import com.wcs.maven.claraxsd.testutils.XsdTestUtils;
import com.wcs.maven.claraxsd.attributebuilder.EnumAttributeBuilder;
import static com.wcs.maven.claraxsd.testutils.XsdTestUtils.writeOptions;
import java.io.StringWriter;
import java.util.Date;
import org.apache.ws.commons.schema.XmlSchema;
import org.apache.ws.commons.schema.XmlSchemaAttribute;
import org.apache.ws.commons.schema.XmlSchemaCollection;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;

/**
 *
 * @author kumm
 */
public class EnumAttributeBuilderTest {
    
    private EnumAttributeBuilder instance;
    private XmlSchema xmlSchema;
    
    @Before
    public void setUp() {
        instance = new EnumAttributeBuilder();
        xmlSchema = new XmlSchema(null, null, new XmlSchemaCollection());
    }
    
    @Test
    public void testBuildAttribute() {
        XmlSchemaAttribute attr = instance.buildAttribute(xmlSchema, "n", MyEnum.class);
        String attributeMarkup = XsdTestUtils.buildAttributeMarkup(xmlSchema, attr);

        String expected 
                = "<attribute name=\"n\">"
                + "<simpleType>"
                + "<restriction base=\"string\">"
                + "<enumeration value=\"ONE\"/>"
                + "<enumeration value=\"TWO\"/>"
                + "<enumeration value=\"THREE\"/>"
                + "</restriction>"
                + "</simpleType>"
                + "</attribute>";
        
        assertEquals(expected, attributeMarkup);
    }

    @Test
    public void testIsSupports() {
        assertFalse(instance.isSupports(Date.class));
        assertTrue(instance.isSupports(MyEnum.class));
    }
    
    public enum MyEnum {
        ONE, TWO, THREE
    }
}
