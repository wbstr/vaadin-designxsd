/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wcs.maven.claraxsd.elementbuilder;

import com.vaadin.ui.Component;
import com.wcs.maven.claraxsd.attributebuilder.AttributeBuilder;
import com.wcs.maven.claraxsd.attributebuilder.AttributeBuilderFactory;
import com.wcs.maven.claraxsd.attributebuilder.NopAttributeBuilder;
import com.wcs.maven.claraxsd.baseattributegroup.BaseAttributeGroup;
import com.wcs.maven.claraxsd.baseattributegroup.BaseAttributeGroupMngr;
import com.wcs.maven.claraxsd.testutils.XsdTestUtils;
import org.apache.ws.commons.schema.XmlSchema;
import org.apache.ws.commons.schema.XmlSchemaAttribute;
import org.apache.ws.commons.schema.XmlSchemaCollection;
import org.apache.ws.commons.schema.XmlSchemaElement;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.xml.namespace.QName;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

/**
 *
 * @author kumm
 */
@RunWith(MockitoJUnitRunner.class)
public class ComponentElementBuilderTest {

    private XmlSchema schema;
    private ComponentElementBuilder instance;
    @Mock
    private AttributeBuilderFactory attributeBuilderFactory;
    @Mock
    private BaseAttributeGroupMngr baseAttributeGroupMngr;

    @Before
    public void setUp() {
        instance = new ComponentElementBuilder(attributeBuilderFactory, baseAttributeGroupMngr);
        schema = new XmlSchema(null, null, new XmlSchemaCollection());
    }

    @Test
    public void testElementNameIsSimpleClassName() {
        when(attributeBuilderFactory.getAttributeBuilder(any(String.class), any(Class.class)))
                .thenReturn(new NopAttributeBuilder());
        XmlSchemaElement result = instance.buildElement(schema, forceCastToComponentClass(MyFakeComponent.class));
        assertEquals("MyFakeComponent", result.getName());
    }

    @Test
    public void testBaseGroupInserted() {
        when(attributeBuilderFactory.getAttributeBuilder(any(String.class), any(Class.class)))
                .thenReturn(new NopAttributeBuilder());
        BaseAttributeGroup baseAttributeGroup = mock(BaseAttributeGroup.class);
        when(baseAttributeGroup.getName()).thenReturn(new QName("MyBaseGroup"));
        when(baseAttributeGroupMngr.findAttributeGroup(MyFakeComponent.class))
         .thenReturn(baseAttributeGroup);

        XmlSchemaElement result = instance.buildElement(schema, forceCastToComponentClass(MyFakeComponent.class));
        String resultMarkup = XsdTestUtils.buildElementMarkup(schema, result);
        String expectedMarkup 
                = "<element name=\"MyFakeComponent\">"
                + "<complexType>"
                + "<attributeGroup ref=\"MyBaseGroup\"/>"
                + "</complexType>"
                + "</element>";

        assertEquals(expectedMarkup, resultMarkup);
    }

    @Test
    public void testAttributesInsertedAlphabetically() {
        when(attributeBuilderFactory.getAttributeBuilder(matches("aProp"), isNull(Class.class)))
                .thenReturn(new MockAttributeBuilder());
        when(attributeBuilderFactory.getAttributeBuilder(matches("[bc]Prop"), eq(String.class)))
                .thenReturn(new MockAttributeBuilder());

        XmlSchemaElement result = instance.buildElement(schema, forceCastToComponentClass(MyFakeComponent.class));
        String resultMarkup = XsdTestUtils.buildElementMarkup(schema, result);
        String expectedMarkup 
                = "<element name=\"MyFakeComponent\">"
                + "<complexType>"
                + "<attribute name=\"aProp\"/>"
                + "<attribute name=\"bProp\"/>"
                + "<attribute name=\"cProp\"/>"
                + "</complexType>"
                + "</element>";

        assertEquals(expectedMarkup, resultMarkup);
    }

    @Test
    public void testInheritedAttributesSkipped() {
        when(attributeBuilderFactory.getAttributeBuilder(matches("[bc]Prop"), eq(String.class)))
                .thenReturn(new MockAttributeBuilder());
        //base group is "MyBaseGroup"
        BaseAttributeGroup baseAttributeGroup = mock(BaseAttributeGroup.class);
        when(baseAttributeGroup.getName()).thenReturn(new QName("MyBaseGroup"));
        when(baseAttributeGroupMngr.findAttributeGroup(MyFakeComponent.class))
         .thenReturn(baseAttributeGroup);
        //aProp is inherited
        when(baseAttributeGroupMngr.isAttributeInherited(baseAttributeGroup, "aProp"))
         .thenReturn(true);

        XmlSchemaElement result = instance.buildElement(schema, forceCastToComponentClass(MyFakeComponent.class));
        String resultMarkup = XsdTestUtils.buildElementMarkup(schema, result);
        String expectedMarkup 
                = "<element name=\"MyFakeComponent\">"
                + "<complexType>"
                + "<attributeGroup ref=\"MyBaseGroup\"/>"
                + "<attribute name=\"bProp\"/>"
                + "<attribute name=\"cProp\"/>"
                + "</complexType>"
                + "</element>";

        assertEquals(expectedMarkup, resultMarkup);
    }

    @Test
    public void testWrongSettersSkipped() {
        verifyZeroInteractions(attributeBuilderFactory);

        XmlSchemaElement result = instance.buildElement(schema, forceCastToComponentClass(MyFakeComponentWithWrongSetters.class));
        String resultMarkup = XsdTestUtils.buildElementMarkup(schema, result);
        String expectedMarkup 
                = "<element name=\"MyFakeComponentWithWrongSetters\">"
                + "<complexType/>"
                + "</element>";

        assertEquals(expectedMarkup, resultMarkup);
    }

    @SuppressWarnings("unchecked")
    private Class<? extends Component> forceCastToComponentClass(Class classToCast) {
        return (Class<? extends Component>)classToCast;
    }

    @SuppressWarnings({"UnusedParameters", "UnusedDeclaration", "EmptyMethod"})
    public static class MyFakeComponent {
        public void setCProp(String prop) {
        }
        public void setAProp() {
        }
        public void setbProp(String prop) {
        }
    }

    @SuppressWarnings({"UnusedParameters", "UnusedDeclaration", "EmptyMethod"})
    public static class MyFakeComponentWithWrongSetters {
        public void dosetCProp(String prop) {
        }
        public void setAProp(String prop, String prop2) {
        }
        private void setbProp(String prop) {
        }
    }
    
    private static class MockAttributeBuilder implements AttributeBuilder {

        @Override
        public XmlSchemaAttribute buildAttribute(XmlSchema schema, String name, Class<?> type) {
            XmlSchemaAttribute attr = new XmlSchemaAttribute();
            attr.setName(name);
            return attr;
        }

        @Override
        public boolean isSupports(Class<?> type) {
            return true;
        }
        
    }
}
