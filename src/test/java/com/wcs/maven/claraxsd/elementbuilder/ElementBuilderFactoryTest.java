/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wcs.maven.claraxsd.elementbuilder;

import com.wcs.maven.claraxsd.elementbuilder.ElementBuilderFactory;
import com.wcs.maven.claraxsd.elementbuilder.ComponentElementBuilder;
import com.wcs.maven.claraxsd.elementbuilder.SingleContainerElementBuilder;
import com.wcs.maven.claraxsd.elementbuilder.NopElementBuilder;
import com.wcs.maven.claraxsd.elementbuilder.ElementBuilder;
import com.wcs.maven.claraxsd.elementbuilder.ContainerElementBuilder;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.wcs.maven.claraxsd.elementbuilder.factorytestclasses.NoArgConstructorLess;
import com.wcs.maven.claraxsd.elementbuilder.factorytestclasses.PublicConstructorLess;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

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
        ElementBuilder result = instance.getElementBuilder(getClass());
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

class MyPackagePrivateComponent {

    public MyPackagePrivateComponent() {
    }
    
}
