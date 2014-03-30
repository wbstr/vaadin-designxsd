/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wcs.maven.claraxsd.baseattributegroup;

import com.wcs.maven.claraxsd.baseattributegroup.BaseAttributeGroupMngr;
import com.wcs.maven.claraxsd.baseattributegroup.BaseSchema;
import com.wcs.maven.claraxsd.baseattributegroup.BaseAttributeGroup;
import com.vaadin.ui.Label;
import com.wcs.maven.claraxsd.baseattributegroup.testclasses.ChildA;
import com.wcs.maven.claraxsd.baseattributegroup.testclasses.ChildB;
import com.wcs.maven.claraxsd.baseattributegroup.testclasses.Parent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.namespace.QName;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import static org.mockito.Mockito.*;
import org.mockito.runners.MockitoJUnitRunner;

/**
 *
 * @author kumm
 */
@RunWith(MockitoJUnitRunner.class)
public class BaseAttributeGroupMngrTest {

    BaseAttributeGroupMngr instance;
    @Mock
    BaseSchema baseSchema;
    Map<QName, BaseAttributeGroup> attributeGroups;

    @Before
    public void setUp() {
        attributeGroups = new HashMap<>();
        when(baseSchema.getAttrGroupsByName()).thenReturn(attributeGroups);
    }

    @Test
    public void testFindAttributeGroup() {
        BaseAttributeGroup childA
                = putBaseAttribureGroup("childA", new String[]{"parent"}, new Class[]{Label.class, ChildA.class});
        BaseAttributeGroup childB
                = putBaseAttribureGroup("childB", new String[]{"parent"}, new Class[]{ChildB.class});
        BaseAttributeGroup parent
                = putBaseAttribureGroup("parent", new String[]{}, new Class[]{ChildA.class, ChildB.class, Parent.class});
        
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
                = putBaseAttribureGroup("childA", new String[]{"parent"}, new String[]{"a_prop"});
        BaseAttributeGroup childB
                = putBaseAttribureGroup("childB", new String[]{"parent"}, new String[]{"b_prop"});
        BaseAttributeGroup parent
                = putBaseAttribureGroup("parent", new String[]{}, new String[]{"p_prop"});

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

    private BaseAttributeGroup putBaseAttribureGroup(String name, String[] referencedNames) {
        BaseAttributeGroup mock = mock(BaseAttributeGroup.class);
        QName qName = new QName(name);
        when(mock.getName()).thenReturn(qName);
        List<QName> referenes = new ArrayList<>();
        for (String refName : referencedNames) {
            referenes.add(new QName(refName));
        }
        when(mock.getReferences()).thenReturn(referenes);
        attributeGroups.put(qName, mock);
        return mock;
    }

    private BaseAttributeGroup putBaseAttribureGroup(String name, String[] referencedNames, Class[] appliesTo) {
        BaseAttributeGroup mock = putBaseAttribureGroup(name, referencedNames);
        for (Class appliesToClass : appliesTo) {
            when(mock.isAppliesTo(appliesToClass)).thenReturn(Boolean.TRUE);
        }
        return mock;
    }

    private BaseAttributeGroup putBaseAttribureGroup(String name, String[] referencedNames, String[] attributes) {
        BaseAttributeGroup mock = putBaseAttribureGroup(name, referencedNames);
        for (String attribute : attributes) {
            when(mock.hasAttribute(attribute)).thenReturn(Boolean.TRUE);
        }
        return mock;
    }
}
