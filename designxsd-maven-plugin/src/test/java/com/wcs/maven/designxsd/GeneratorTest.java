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
package com.wcs.maven.designxsd;

import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;


/**
 *
 * @author kumm
 */
@RunWith(MockitoJUnitRunner.class)
@Ignore
public class GeneratorTest {

//    private Generator generator;
//    @Mock
//    Generator.GeneratedSchemaFactory generatedSchemaFactory;
//    @Mock
//    ElementBuilderFactory elementBuilderFactory;
//    GeneratedSchema generatedSchema;
//    private TestElementBuilder testElementBuilder;
//
//    @Before
//    public void setUp() {
//        generator = new Generator(generatedSchemaFactory);
//        generatedSchema = new GeneratedSchema(elementBuilderFactory);
//        when(generatedSchemaFactory.newGeneratedSchema()).thenReturn(generatedSchema);
//        testElementBuilder = new TestElementBuilder();
//        when(elementBuilderFactory.getElementBuilder(Mockito.any())).thenReturn(testElementBuilder);
//    }
//
//    @Test
//    public void testPackageScan() {
//        generator.generate("com.vaadin.ui");
//
//        for (Class<? extends Component> aClass : testElementBuilder.classes) {
//            String packageName = aClass.getPackage().getName();
//            assertEquals("com.vaadin.ui", packageName);
//        }
//
//    }
//
//    private class TestElementBuilder extends DumbElementBuilder {
//        List<Class<? extends Component>> classes = new LinkedList<>();
//
//        public XmlSchemaElement buildElement(XmlSchema schema, Class componentClass) {
//            classes.add(componentClass);
//            return super.buildElement(schema, componentClass);
//        }
//    }
//
//
//    public static class MyFakeComponent extends AbstractComponent {
//        
//    }

}
