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

import com.vaadin.ui.*;
import com.wcs.maven.claraxsd.elementbuilder.factorytestclasses.NoArgConstructorLess;
import com.wcs.maven.claraxsd.elementbuilder.factorytestclasses.PublicConstructorLess;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 *
 * @author kumm
 */
public class ElementBuilderFactoryTest {

    private ElementBuilderFactory instance;

    @Before
    public void setUp() {
        instance = new ElementBuilderFactory(null, null);
    }
    
    @Test
    public void testVaadinComponentElementBuilder() {
        ElementBuilder result = instance.getElementBuilder(Label.class);
        assertTrue(result instanceof ComponentElementBuilder);
    }

    @Test
    public void testVaadinContainerElementBuilder() {
        ElementBuilder result = instance.getElementBuilder(VerticalLayout.class);
        assertTrue(result instanceof ContainerElementBuilder);
    }

    @Test
    public void testVaadinSingleContainerElementBuilder() {
        ElementBuilder result = instance.getElementBuilder(Panel.class);
        assertTrue(result instanceof SingleContainerElementBuilder);
    }

    @Test
    public void testNotVaadinComponentProducesNop() {
        @SuppressWarnings("unchecked")
        ElementBuilder result = instance.getElementBuilder((Class<? extends Component>)getClass());
        assertTrue(result instanceof NopElementBuilder);
    }

    @Test
    public void testVaadinUIProducesNop() {
        ElementBuilder result = instance.getElementBuilder(UI.class);
        assertTrue(result instanceof NopElementBuilder);
    }

    @Test
    public void testInterfaceProducesNop() {
        ElementBuilder result = instance.getElementBuilder(Component.class);
        assertTrue(result instanceof NopElementBuilder);
    }

    @Test
    public void testAbstractClassProducesNop() {
        ElementBuilder result = instance.getElementBuilder(AbstractComponent.class);
        assertTrue(result instanceof NopElementBuilder);
    }
    
    @Test
    public void testWithoutNoArgConstructorProducesNop() {
        ElementBuilder result = instance.getElementBuilder(NoArgConstructorLess.class);
        assertTrue(result instanceof NopElementBuilder);
    }
    
    @Test
    public void testInnerClassProducesNop() {
        ElementBuilder result = instance.getElementBuilder(MyInnerComponent.class);
        assertTrue(result instanceof NopElementBuilder);
    }
    
    @Test
    public void testInnerStaticClassProducesNop() {
        ElementBuilder result = instance.getElementBuilder(MyInnerStaticComponent.class);
        assertTrue(result instanceof NopElementBuilder);
    }
    
    @Test
    public void testWithoutPublicConstructorProducesNop() {
        ElementBuilder result = instance.getElementBuilder(PublicConstructorLess.class);
        assertTrue(result instanceof NopElementBuilder);
    }
    
    @Test
    public void testPackagePrivateProducesNop() {
        ElementBuilder result = instance.getElementBuilder(MyPackagePrivateComponent.class);
        assertTrue(result instanceof NopElementBuilder);
    }

    public class MyInnerComponent extends AbstractComponent {
        
    }

    public static class MyInnerStaticComponent extends AbstractComponent {
        
    }
}

class MyPackagePrivateComponent extends AbstractComponent {

    public MyPackagePrivateComponent() {
    }
    
}
