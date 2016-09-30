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

import com.vaadin.ui.Label;
import com.wcs.maven.designxsd.baseattributegroup.testclasses.ChildA;
import com.wcs.maven.designxsd.baseattributegroup.testclasses.ChildB;
import com.wcs.maven.designxsd.baseattributegroup.testclasses.ChildC;
import com.wcs.maven.designxsd.baseattributegroup.testclasses.Parent;
import com.wcs.maven.designxsd.baseattributegroup.testclasses.Parent.Interf;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.xml.namespace.QName;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 *
 * @author kumm
 */
@RunWith(MockitoJUnitRunner.class)
public class BaseAttributeGroupMngrTest {

    private BaseAttributeGroupMngr instance;
    @Mock
    BaseSchema baseSchema;
    private Map<QName, BaseAttributeGroup> attributeGroups;

    @Before
    public void setUp() {
        attributeGroups = new HashMap<>();
        when(baseSchema.getAttrGroupsByName()).thenReturn(attributeGroups);
    }

    @Test
    public void testFindAttributeGroup() {
        BaseAttributeGroup childA
                = putBaseAttributeGroup("childA", new String[]{"parent"}, new Class[]{Label.class, ChildA.class, ChildC.class});
        BaseAttributeGroup childB
                = putBaseAttributeGroup("childB", new String[]{"parent"}, new Class[]{ChildB.class});
        BaseAttributeGroup childC
                = putBaseAttributeGroup("childC", new String[]{"childA"}, new Class[]{ChildC.class, Interf.class});
        BaseAttributeGroup parent
                = putBaseAttributeGroup("parent", new String[]{}, new Class[]{ChildA.class, ChildB.class, Parent.class});
        BaseAttributeGroup interf
                = putBaseAttributeGroup("interf", new String[]{}, new Class[]{ChildC.class});
        
        instance = new BaseAttributeGroupMngr(baseSchema);
        
        assertArrayEquals(new BaseAttributeGroup[] {childA}, instance.findAttributeGroup(Label.class).toArray());
        assertArrayEquals(new BaseAttributeGroup[] {childA}, instance.findAttributeGroup(ChildA.class).toArray());
        assertArrayEquals(new BaseAttributeGroup[] {childB}, instance.findAttributeGroup(ChildB.class).toArray());
        assertArrayEquals(new BaseAttributeGroup[] {childC, interf}, instance.findAttributeGroup(ChildC.class).toArray());
        assertArrayEquals(new BaseAttributeGroup[] {parent}, instance.findAttributeGroup(Parent.class).toArray());
        assertTrue(instance.findAttributeGroup(getClass()).isEmpty());
    }

    @Test
    public void testIsAttributeInherited() {
        BaseAttributeGroup childA
                = putBaseAttributeGroup("childA", new String[]{"parent"}, new String[]{"a_prop"});
        BaseAttributeGroup childB
                = putBaseAttributeGroup("childB", new String[]{"parent"}, new String[]{"b_prop"});
        BaseAttributeGroup parent
                = putBaseAttributeGroup("parent", new String[]{}, new String[]{"p_prop"});

        instance = new BaseAttributeGroupMngr(baseSchema);

        assertTrue(instance.isAttributeInherited(childA, "a_prop"));
        assertFalse(instance.isAttributeInherited(childA, "b_prop"));
        assertTrue(instance.isAttributeInherited(childA, "p_prop"));

        assertTrue(instance.isAttributeInherited(childB, "b_prop"));
        assertFalse(instance.isAttributeInherited(childB, "a_prop"));
        assertTrue(instance.isAttributeInherited(childB, "p_prop"));

        assertFalse(instance.isAttributeInherited(parent, "a_prop"));
        assertFalse(instance.isAttributeInherited(parent, "b_prop"));
        assertTrue(instance.isAttributeInherited(parent, "p_prop"));
    }

    private BaseAttributeGroup putBaseAttributeGroup(String name, String[] referencedNames) {
        BaseAttributeGroup mock = mock(BaseAttributeGroup.class);
        QName qName = new QName(name);
        when(mock.getName()).thenReturn(qName);
        List<QName> references = new ArrayList<>();
        for (String refName : referencedNames) {
            references.add(new QName(refName));
        }
        when(mock.getReferences()).thenReturn(references);
        attributeGroups.put(qName, mock);
        return mock;
    }

    private BaseAttributeGroup putBaseAttributeGroup(String name, String[] referencedNames, Class[] appliesTo) {
        BaseAttributeGroup mock = putBaseAttributeGroup(name, referencedNames);
        for (Class appliesToClass : appliesTo) {
            when(mock.isAppliesTo(appliesToClass)).thenReturn(Boolean.TRUE);
        }
        return mock;
    }

    private BaseAttributeGroup putBaseAttributeGroup(String name, String[] referencedNames, String[] attributes) {
        BaseAttributeGroup mock = putBaseAttributeGroup(name, referencedNames);
        for (String attribute : attributes) {
            when(mock.hasAttribute(attribute)).thenReturn(Boolean.TRUE);
        }
        return mock;
    }
}
