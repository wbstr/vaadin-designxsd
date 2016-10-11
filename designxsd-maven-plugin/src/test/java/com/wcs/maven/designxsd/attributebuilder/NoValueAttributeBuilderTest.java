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
package com.wcs.maven.designxsd.attributebuilder;

import com.wcs.maven.designxsd.testutils.XsdTestUtils;
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
