/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.wcs.wcslib.vaadin.claraxsd;

import static com.wcs.wcslib.vaadin.claraxsd.XsdTestUtils.writeOptions;
import java.io.StringWriter;
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
public class PrimitiveAttributeProducerTest {
    
    PrimitiveAttributeProducer producer;
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

    public PrimitiveAttributeProducerTest(Class<?> type, boolean supported, String xsdType) {
        this.supported = supported;
        this.xsdType = xsdType;
        this.type = type;
    }
    XmlSchema xmlSchema;
    
    @Before
    public void setUp() {
        producer = new PrimitiveAttributeProducer();
/*        XmlSchemaCollection xmlSchemaCollection = new XmlSchemaCollection();
        xmlSchemaCollection.*/
        xmlSchema = new XmlSchema(new XmlSchemaCollection());
    }
    
    @Test
    public void testProduce() {
        XmlSchemaAttribute attr = producer.produce("n", type);
        if (!supported) {
            assertNull(attr);
            return;
        }
        xmlSchema.getItems().add(attr);
        StringWriter markupWriter = new StringWriter();
        xmlSchema.write(markupWriter, writeOptions);
        String markup = markupWriter.toString();
        int endIndex = markup.indexOf("</schema>");
        int beginIndex = markup.indexOf("<attribute ");
        String attributeMarkup = markup.substring(beginIndex, endIndex);
        assertEquals("<attribute name=\"n\" type=\""+xsdType+"\"/>", attributeMarkup);
    }

    @Test
    public void testIsSupports() {
        assertEquals(supported, producer.isSupports(type));
    }
    
}
