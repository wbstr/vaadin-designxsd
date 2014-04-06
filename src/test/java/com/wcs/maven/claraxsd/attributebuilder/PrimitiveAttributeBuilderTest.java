/*
 * Copyright 2014 Webstar Csoport Kft.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.wcs.maven.claraxsd.attributebuilder;

import com.wcs.maven.claraxsd.testutils.XsdTestUtils;
import org.apache.ws.commons.schema.XmlSchema;
import org.apache.ws.commons.schema.XmlSchemaAttribute;
import org.apache.ws.commons.schema.XmlSchemaCollection;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;

import static org.junit.Assert.assertEquals;

/**
 *
 * @author kumm
 */
@RunWith(Parameterized.class)
public class PrimitiveAttributeBuilderTest {
    
    private PrimitiveAttributeBuilder producer;
    private final Class<?> type;
    private final boolean supported;
    private final String xsdType;
    
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
    private XmlSchema xmlSchema;
    
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
