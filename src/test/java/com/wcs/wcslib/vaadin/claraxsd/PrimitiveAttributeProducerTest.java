/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.wcs.wcslib.vaadin.claraxsd;

import java.util.Arrays;
import java.util.Collection;
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
            {Object.class, true, "string"}
        });
    }

    public void PrimitiveAttributeProducerTest(Class<?> type, boolean supported, String xsdType) {
        this.supported = supported;
        this.xsdType = xsdType;
        this.type = type;
    }
    
    @Before
    public void setUp() {
        producer = new PrimitiveAttributeProducer();
    }
    
    @Test
    public void testProduce() {
        assertEquals("<xs:atribute name=\"n\" type=\"xs:"+xsdType+"\"/>", producer.produce("n", type).toString());
    }

    @Test
    public void testIsSupports() {
        assertEquals(supported, producer.isSupports(type));
    }
    
}
