/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wcs.maven.claraxsd.baseattributegroup;

import com.vaadin.ui.Label;
import com.wcs.maven.claraxsd.baseattributegroup.testclasses.ChildA;
import com.wcs.maven.claraxsd.baseattributegroup.testclasses.ChildB;
import com.wcs.maven.claraxsd.baseattributegroup.testclasses.Parent;
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
                = putBaseAttributeGroup("childA", new String[]{"parent"}, new Class[]{Label.class, ChildA.class});
        BaseAttributeGroup childB
                = putBaseAttributeGroup("childB", new String[]{"parent"}, new Class[]{ChildB.class});
        BaseAttributeGroup parent
                = putBaseAttributeGroup("parent", new String[]{}, new Class[]{ChildA.class, ChildB.class, Parent.class});
        
        instance = new BaseAttributeGroupMngr(baseSchema);
        
        assertEquals(childA, instance.findAttributeGroup(Label.class));
        assertEquals(childA, instance.findAttributeGroup(ChildA.class));
        assertEquals(childB, instance.findAttributeGroup(ChildB.class));
        assertEquals(parent, instance.findAttributeGroup(Parent.class));
        assertNull(instance.findAttributeGroup(getClass()));
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
