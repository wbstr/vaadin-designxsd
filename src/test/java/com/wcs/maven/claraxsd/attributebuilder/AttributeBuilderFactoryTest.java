/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wcs.maven.claraxsd.attributebuilder;

import com.wcs.maven.claraxsd.attributebuilder.AttributeBuilderFactory;
import com.wcs.maven.claraxsd.attributebuilder.AttributeBuilder;
import com.wcs.maven.claraxsd.attributebuilder.NopAttributeBuilder;
import java.util.Arrays;
import org.apache.ws.commons.schema.XmlSchema;
import org.apache.ws.commons.schema.XmlSchemaAttribute;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author kumm
 */
public class AttributeBuilderFactoryTest {

    private AttributeBuilderFactory instance;

    @Before
    public void setUp() {
        instance = new AttributeBuilderFactory();
    }

    @Test
    public void testDebugIdProducesNop() {
        AttributeBuilder result = instance.getAttributeBuilder("debugId", String.class);
        assertTrue(result instanceof NopAttributeBuilder);
    }

    @Test
    public void testReturnsNopOnNoMatch() {
        instance.builders = Arrays.asList();
        AttributeBuilder result = instance.getAttributeBuilder("something", Integer.TYPE);
        assertTrue(result instanceof NopAttributeBuilder);
    }

    @Test
    public void testReturnsMatching() {
        AttributeBuilder matchesNoneAttributeBuilder = new AttributeBuilder() {

            @Override
            public XmlSchemaAttribute buildAttribute(XmlSchema schema, String name, Class<?> type) {
                throw new UnsupportedOperationException("Not supported");
            }

            @Override
            public boolean isSupports(Class<?> type) {
                return false;
            }
        };
        AttributeBuilder matchesAllAttributeBuilder = new AttributeBuilder() {

            @Override
            public XmlSchemaAttribute buildAttribute(XmlSchema schema, String name, Class<?> type) {
                throw new UnsupportedOperationException("Not supported");
            }

            @Override
            public boolean isSupports(Class<?> type) {
                return true;
            }
        };
        instance.builders = Arrays.asList(matchesNoneAttributeBuilder, matchesAllAttributeBuilder);
        AttributeBuilder result = instance.getAttributeBuilder("something", Integer.TYPE);
        assertTrue(result == matchesAllAttributeBuilder);
    }

}
