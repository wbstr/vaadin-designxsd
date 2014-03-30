/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.wcs.maven.claraxsd.attributebuilder;

import com.wcs.maven.claraxsd.testutils.XsdTestUtils;
import com.wcs.maven.claraxsd.attributebuilder.PrimitiveAttributeBuilder;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import org.apache.ws.commons.schema.XmlSchema;
import org.apache.ws.commons.schema.XmlSchemaAttribute;
import org.apache.ws.commons.schema.XmlSchemaCollection;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

/**
 *
 * @author kumm
 */
@RunWith(Parameterized.class)
public class PrimitiveAttributeBuilderTest {
    
    PrimitiveAttributeBuilder producer;
    Class<?> type;
    boolean supported;
    String xsdType;
    
    @Parameterized.Parameters
    public static Collection getData() {
        return Arrays.asList(new Object[][] {
            {Boolean.class, true, "boolean"},
            {Boolean.TYPE, true, "boolean"},
            {Byte.TYPE, true, "byte"},
            {Byte.class, true, "byte"},
            {Integer.TYPE, true, "int"},
            {Integer.class, true, "int"},
            {Long.TYPE, true, "long"},
            {Long.class, true, "long"},
            {Float.TYPE, true, "float"},
            {Float.class, true, "float"},
            {Double.TYPE, true, "double"},
            {Double.class, true, "double"},
            {String.class, true, "string"},
            {Object.class, true, "string"},
            {Date.class, false, null}
        });
    }

    public PrimitiveAttributeBuilderTest(Class<?> type, boolean supported, String xsdType) {
        this.supported = supported;
        this.xsdType = xsdType;
        this.type = type;
    }
    XmlSchema xmlSchema;
    
    @Before
    public void setUp() {
        producer = new PrimitiveAttributeBuilder();
        xmlSchema = new XmlSchema(null, null, new XmlSchemaCollection());
    }
    
    @Test
    public void testProduce() {
        if (!supported) {
            return;
        }
        XmlSchemaAttribute attr = producer.buildAttribute(xmlSchema, "n", type);
        String attributeMarkup = XsdTestUtils.buildAttributeMarkup(xmlSchema, attr);
        assertEquals("<attribute name=\"n\" type=\""+xsdType+"\"/>", attributeMarkup);
    }

    @Test
    public void testIsSupports() {
        assertEquals(supported, producer.isSupports(type));
    }
    
}
