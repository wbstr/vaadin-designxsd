/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wcs.maven.claraxsd.attributebuilder;

import com.wcs.maven.claraxsd.attributebuilder.NoValueAttributeBuilder;
import com.wcs.maven.claraxsd.testutils.XsdTestUtils;
import org.apache.ws.commons.schema.XmlSchema;
import org.apache.ws.commons.schema.XmlSchemaAttribute;
import org.apache.ws.commons.schema.XmlSchemaCollection;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author kumm
 */
public class NoValueAttributeBuilderTest {

    private NoValueAttributeBuilder instance;
    private XmlSchema xmlSchema;

    @Before
    public void setUp() {
        instance = new NoValueAttributeBuilder();
        xmlSchema = new XmlSchema(null, null, new XmlSchemaCollection());
    }

    @Test
    public void testBuildAttribute() {
        XmlSchemaAttribute attr = instance.buildAttribute(xmlSchema, "n", null);
        String attributeMarkup = XsdTestUtils.buildAttributeMarkup(xmlSchema, attr);
        String expected  = "<attribute fixed=\"\" name=\"n\" type=\"string\"/>";
        assertEquals(expected, attributeMarkup);
    }

    @Test
    public void testIsSupports() {
        assertTrue(instance.isSupports(null));
        assertFalse(instance.isSupports(getClass()));
    }

}
