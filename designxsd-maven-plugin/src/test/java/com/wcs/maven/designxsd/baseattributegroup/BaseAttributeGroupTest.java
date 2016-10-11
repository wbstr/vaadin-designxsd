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

package com.wcs.maven.designxsd.baseattributegroup;

import com.wcs.maven.designxsd.baseattributegroup.testclasses.Parent;
import com.wcs.maven.designxsd.baseattributegroup.testclasses.Parent.Interf;
import javax.xml.namespace.QName;
import org.apache.ws.commons.schema.XmlSchemaAttributeGroup;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author kumm
 */
public class BaseAttributeGroupTest {

    @Test
    public void testDoubleDotInNameDefinesInnerClass() {
        XmlSchemaAttributeGroup xmlSchemaAttributeGroup = new XmlSchemaAttributeGroup();
        xmlSchemaAttributeGroup.setName(new QName(Parent.class.getCanonicalName()+"..Interf"));
        BaseAttributeGroup baseAttributeGroup = new BaseAttributeGroup(xmlSchemaAttributeGroup);
        Class<?> groupClass = baseAttributeGroup.getGroupClass();
        assertEquals (Interf.class, groupClass);
    }
    
    @Test
    public void testNameDefinesClass() {
        XmlSchemaAttributeGroup xmlSchemaAttributeGroup = new XmlSchemaAttributeGroup();
        xmlSchemaAttributeGroup.setName(new QName(Parent.class.getCanonicalName()));
        BaseAttributeGroup baseAttributeGroup = new BaseAttributeGroup(xmlSchemaAttributeGroup);
        Class<?> groupClass = baseAttributeGroup.getGroupClass();
        assertEquals (Parent.class, groupClass);
    }
}
