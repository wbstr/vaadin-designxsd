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

import java.util.Date;

import static org.junit.Assert.*;

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

    @SuppressWarnings("UnusedDeclaration")
    public enum MyEnum {
        ONE, TWO, THREE
    }
}
