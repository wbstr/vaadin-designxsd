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

package com.wcs.maven.claraxsd.baseattributegroup;

import com.wcs.maven.claraxsd.baseattributegroup.testclasses.ChildA;
import com.wcs.maven.claraxsd.baseattributegroup.testclasses.ChildB;
import com.wcs.maven.claraxsd.baseattributegroup.testclasses.Parent;
import org.apache.ws.commons.schema.XmlSchema;
import org.apache.ws.commons.schema.XmlSchemaCollection;
import org.junit.Before;
import org.junit.Test;
import org.xml.sax.InputSource;

import javax.xml.namespace.QName;
import java.util.Arrays;
import java.util.Map;

import static org.junit.Assert.*;

/**
 *
 * @author kumm
 */
public class BaseSchemaTest {

    private BaseSchema instance;
    
    @Before
    public void setUp() {
        XmlSchemaCollection schemaColl = new XmlSchemaCollection();
        XmlSchema schema = schemaColl.read(new InputSource(getClass().getResourceAsStream("base_schema_test.xsd")), null);
        instance = new BaseSchema(schema);
    }

    @Test
    public void testLoaded() {
        Map<QName, BaseAttributeGroup> result = instance.getAttrGroupsByName();
        assertEquals(3, result.size());
    }
    
    @Test
    public void testBaseAttributeGroup() {
        Map<QName, BaseAttributeGroup> result = instance.getAttrGroupsByName();
        BaseAttributeGroup childAGroup = result.get(getGroupQName(ChildA.class));
        assertNotNull(childAGroup);
        assertEquals(getGroupQName(ChildA.class), childAGroup.getName());
        assertEquals(ChildA.class, childAGroup.getGroupClass());
        assertTrue(childAGroup.hasAttribute("a_prop"));
        assertFalse(childAGroup.hasAttribute("p_prop"));
        assertEquals(Arrays.asList(getGroupQName(Parent.class)), childAGroup.getReferences());
    }
    
    @Test
    public void testBaseAttributeGroupAppliesTo() {
        Map<QName, BaseAttributeGroup> result = instance.getAttrGroupsByName();
        BaseAttributeGroup childAGroup = result.get(getGroupQName(ChildA.class));
        assertFalse(childAGroup.isAppliesTo(ChildB.class));
        assertFalse(childAGroup.isAppliesTo(Parent.class));
        assertTrue(childAGroup.isAppliesTo(ChildA.class));
        BaseAttributeGroup parentGroup = result.get(getGroupQName(Parent.class));
        assertTrue(parentGroup.isAppliesTo(ChildA.class));
        assertTrue(parentGroup.isAppliesTo(ChildB.class));
        assertTrue(parentGroup.isAppliesTo(Parent.class));
    }

    private QName getGroupQName(Class componentClass) {
        return new QName("urn:clara:base", componentClass.getCanonicalName());
    }
    
}
