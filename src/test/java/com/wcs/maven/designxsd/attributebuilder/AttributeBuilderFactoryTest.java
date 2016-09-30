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

import org.apache.ws.commons.schema.XmlSchema;
import org.apache.ws.commons.schema.XmlSchemaAttribute;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertTrue;

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
