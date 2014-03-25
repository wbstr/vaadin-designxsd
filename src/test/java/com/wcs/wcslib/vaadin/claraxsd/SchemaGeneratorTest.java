/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wcs.wcslib.vaadin.claraxsd;

import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.AbstractField;
import com.vaadin.ui.Component;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import java.util.Collection;
import java.util.List;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;

/**
 *
 * @author kumm
 */
public class SchemaGeneratorTest {

    Generator generator;

    @Before
    public void setUp() {
        generator = new Generator();
    }

    @Test
    public void testAtributeGroupsParsed() {
        List<Generator.AttributeGroup> attributeGroups = generator.getAttributeGroups();

        assertEquals(3, attributeGroups.size());
        assertEquals(AbstractField.class, attributeGroups.get(0).getGroupClass());
        assertEquals(AbstractComponent.class, attributeGroups.get(1).getGroupClass());
        assertEquals(Component.class, attributeGroups.get(2).getGroupClass());
    }

    @Test
    public void testFindAttributeGroup() {
        assertEquals(AbstractField.class, generator.findAttributeGroup(TextField.class).getGroupClass());
        assertEquals(AbstractComponent.class, generator.findAttributeGroup(VerticalLayout.class).getGroupClass());
    }

    @Test
    public void testAppendCreatesXsdPerPackage() {
        generator.append(VerticalLayout.class);
        generator.append(MyComponent.class);

        Collection<GeneratedSchema> generatedSchemas = generator.getGeneratedSchemas();
        assertEquals(2, generatedSchemas.size());
    }

}
