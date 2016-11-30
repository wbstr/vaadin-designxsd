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
package com.wcs.maven.designxsd.elementbuilder;

import com.vaadin.ui.Component;
import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.SingleComponentContainer;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.UI;
import com.vaadin.ui.declarative.DesignContext;
import com.wcs.maven.designxsd.attributebuilder.AttributeBuilderFactory;
import com.wcs.maven.designxsd.baseattributegroup.BaseAttributeGroupMngr;
import com.wcs.maven.designxsd.discoverer.ColGroupDiscoverer;
import com.wcs.maven.designxsd.discoverer.HtmlContentDiscoverer;
import com.wcs.maven.designxsd.discoverer.NodeDiscoverer;
import com.wcs.maven.designxsd.discoverer.OptionDiscoverer;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;

public class ElementBuilderFactory {

    private final AttributeBuilderFactory attributeBuilderFactory;
    private final BaseAttributeGroupMngr baseAttributeGroupMngr;
    private final DesignContext designContext;

    public ElementBuilderFactory(
            AttributeBuilderFactory attributeBuilderFactory,
            BaseAttributeGroupMngr baseAttributeGroupMngr,
            DesignContext designContext) {
        this.attributeBuilderFactory = attributeBuilderFactory;
        this.baseAttributeGroupMngr = baseAttributeGroupMngr;
        this.designContext = designContext;
    }

    public ElementBuilder getElementBuilder(Class<? extends Component> componentClass) {
        if (!isVaadinComponentSupportedByDesign(componentClass)) {
            return new NopElementBuilder();
        }

        if (TabSheet.class.getName().equals(componentClass.getName())) {
            return new TabSheetElementBuilder(attributeBuilderFactory, baseAttributeGroupMngr, designContext);
        }
        
        if (ComponentContainer.class.isAssignableFrom(componentClass)) {
            return new ContainerElementBuilder(attributeBuilderFactory, baseAttributeGroupMngr, designContext);
        }

        if (SingleComponentContainer.class.isAssignableFrom(componentClass)) {
            return new SingleContainerElementBuilder(attributeBuilderFactory, baseAttributeGroupMngr, designContext);
        }

        if (new HtmlContentDiscoverer().discover(componentClass)) {
            return new HtmlComponentElementBuilder(attributeBuilderFactory, baseAttributeGroupMngr, designContext);
        }

        if (new OptionDiscoverer().discover(componentClass)) {
            return new OptionComponentElementBuilder(attributeBuilderFactory, baseAttributeGroupMngr, designContext);
        }

        if (new ColGroupDiscoverer().discover(componentClass)) {
            return new ColGroupComponentElementBuilder(attributeBuilderFactory, baseAttributeGroupMngr, designContext);
        }

        if (new NodeDiscoverer().discover(componentClass)) {
            return new NodeComponentElementBuilder(attributeBuilderFactory, baseAttributeGroupMngr, designContext);
        }

        return new ComponentElementBuilder(attributeBuilderFactory, baseAttributeGroupMngr, designContext);
    }

    private boolean isVaadinComponentSupportedByDesign(Class<? extends Component> componentClass) {
        if (!Component.class.isAssignableFrom(componentClass)) {
            return false;
        }
        if (UI.class.isAssignableFrom(componentClass)) {
            return false;
        }
        if (componentClass.isMemberClass()) {
            return false;
        }
        if (Modifier.isAbstract(componentClass.getModifiers())
                || Modifier.isInterface(componentClass.getModifiers())
                || !Modifier.isPublic(componentClass.getModifiers())) {
            return false;
        }
        try {
            Constructor<? extends Component> constructor = componentClass.getConstructor();
            if (constructor == null || !Modifier.isPublic(constructor.getModifiers())) {
                return false;
            }
        } catch (NoSuchMethodException e) {
            return false;
        }
        return true;
    }

    public boolean isPackageRegistered(Package componentPackage) {
        return designContext.getPackagePrefix(componentPackage.getName()) != null;
    }
}
