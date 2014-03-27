/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.wcs.wcslib.vaadin.claraxsd.attributeproducer;

import com.wcs.wcslib.vaadin.claraxsd.XsdTestUtils;
import com.wcs.wcslib.vaadin.claraxsd.attributeproducer.EnumAttributeProducer;
import static com.wcs.wcslib.vaadin.claraxsd.XsdTestUtils.writeOptions;
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
public class EnumerationAttributeProducerTest {
    
    EnumAttributeProducer producer;
    XmlSchema xmlSchema;
    
    @Before
    public void setUp() {
        producer = new EnumAttributeProducer();
        xmlSchema = new XmlSchema(null, null, new XmlSchemaCollection());
    }
    
    @Test
    public void testProduce() {
        XmlSchemaAttribute attr = producer.produce(xmlSchema, "n", MyEnum.class);
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
        assertFalse(producer.isSupports(Date.class));
        assertTrue(producer.isSupports(MyEnum.class));
    }
    
    public enum MyEnum {
        ONE, TWO, THREE
    }
}
