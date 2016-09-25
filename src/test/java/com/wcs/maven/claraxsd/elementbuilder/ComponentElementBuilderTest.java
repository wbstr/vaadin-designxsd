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
package com.wcs.maven.claraxsd.elementbuilder;

import com.vaadin.ui.Component;
import com.vaadin.ui.TextField;
import com.vaadin.ui.declarative.DesignContext;
import com.wcs.maven.claraxsd.attributebuilder.AttributeBuilder;
import com.wcs.maven.claraxsd.attributebuilder.AttributeBuilderFactory;
import com.wcs.maven.claraxsd.attributebuilder.NopAttributeBuilder;
import com.wcs.maven.claraxsd.baseattributegroup.BaseAttributeGroup;
import com.wcs.maven.claraxsd.baseattributegroup.BaseAttributeGroupMngr;
import com.wcs.maven.claraxsd.testutils.XsdTestUtils;
import java.util.Arrays;
import org.apache.ws.commons.schema.XmlSchema;
import org.apache.ws.commons.schema.XmlSchemaAttribute;
import org.apache.ws.commons.schema.XmlSchemaCollection;
import org.apache.ws.commons.schema.XmlSchemaElement;
import org.junit.Before;
import org.junit.Ignore;
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
        DesignContext designContext = new DesignContext();
        designContext.addPackagePrefix("custom", TestComponent.class.getPackage().getName());
        instance = new ComponentElementBuilder(attributeBuilderFactory, baseAttributeGroupMngr, designContext);
        schema = new XmlSchema(null, null, new XmlSchemaCollection());
    }

    @Test
    public void testElementNameDefaultPrefix() {
        when(attributeBuilderFactory.getAttributeBuilder(any(String.class), any(Class.class)))
                .thenReturn(new NopAttributeBuilder());
        XmlSchemaElement result = instance.buildElement(schema, TextField.class);
        assertEquals("vaadin-text-field", result.getName());
    }

    @Test
    public void testElementNameCustomPrefix() {
        when(attributeBuilderFactory.getAttributeBuilder(any(String.class), any(Class.class)))
                .thenReturn(new NopAttributeBuilder());
        XmlSchemaElement result = instance.buildElement(schema, TestComponent.class);
        assertEquals("custom-test-component", result.getName());
    }

    @Test
    public void testBaseGroupInserted() {
        when(attributeBuilderFactory.getAttributeBuilder(any(String.class), any(Class.class)))
                .thenReturn(new NopAttributeBuilder());
        BaseAttributeGroup baseAttributeGroup1 = mock(BaseAttributeGroup.class);
        when(baseAttributeGroup1.getName()).thenReturn(new QName("MyBaseGroup1"));
        BaseAttributeGroup baseAttributeGroup2 = mock(BaseAttributeGroup.class);
        when(baseAttributeGroup2.getName()).thenReturn(new QName("MyBaseGroup2"));
        when(baseAttributeGroupMngr.findAttributeGroup(TestComponent.class))
         .thenReturn(Arrays.asList(baseAttributeGroup1, baseAttributeGroup2));

        XmlSchemaElement result = instance.buildElement(schema, TestComponent.class);
        String resultMarkup = XsdTestUtils.buildElementMarkup(schema, result);
        String expectedMarkup 
                = "<element name=\"custom-test-component\">"
                + "<complexType>"
                + "<attributeGroup ref=\"MyBaseGroup1\"/>"
                + "<attributeGroup ref=\"MyBaseGroup2\"/>"
                + "</complexType>"
                + "</element>";

        assertEquals(expectedMarkup, resultMarkup);
    }

    @Test
    @Ignore
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
    @Ignore
    public void testInheritedAttributesSkipped() {
        when(attributeBuilderFactory.getAttributeBuilder(matches("[bc]Prop"), eq(String.class)))
                .thenReturn(new MockAttributeBuilder());
        //base group is "MyBaseGroup"
        BaseAttributeGroup baseAttributeGroup = mock(BaseAttributeGroup.class);
        when(baseAttributeGroup.getName()).thenReturn(new QName("MyBaseGroup1"));
        BaseAttributeGroup baseAttributeGroup2 = mock(BaseAttributeGroup.class);
        when(baseAttributeGroup2.getName()).thenReturn(new QName("MyBaseGroup2"));
        when(baseAttributeGroupMngr.findAttributeGroup(MyFakeComponent.class))
         .thenReturn(Arrays.asList(baseAttributeGroup, baseAttributeGroup2));
        //aProp is inherited
        when(baseAttributeGroupMngr.isAttributeInherited(baseAttributeGroup, "aProp"))
         .thenReturn(true);
        //bProp is inherited
        when(baseAttributeGroupMngr.isAttributeInherited(baseAttributeGroup2, "bProp"))
         .thenReturn(true);

        XmlSchemaElement result = instance.buildElement(schema, forceCastToComponentClass(MyFakeComponent.class));
        String resultMarkup = XsdTestUtils.buildElementMarkup(schema, result);
        String expectedMarkup 
                = "<element name=\"MyFakeComponent\">"
                + "<complexType>"
                + "<attributeGroup ref=\"MyBaseGroup1\"/>"
                + "<attributeGroup ref=\"MyBaseGroup2\"/>"
                + "<attribute name=\"cProp\"/>"
                + "</complexType>"
                + "</element>";

        assertEquals(expectedMarkup, resultMarkup);
    }

    @Test
    @Ignore
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
