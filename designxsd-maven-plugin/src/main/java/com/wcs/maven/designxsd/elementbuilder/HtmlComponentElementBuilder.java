/*
 * Copyright 2016 lali.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.wcs.maven.designxsd.elementbuilder;

import com.vaadin.ui.Component;
import com.vaadin.ui.declarative.DesignContext;
import com.wcs.maven.designxsd.attributebuilder.AttributeBuilderFactory;
import com.wcs.maven.designxsd.baseattributegroup.BaseAttributeGroupMngr;
import javax.xml.namespace.QName;
import org.apache.ws.commons.schema.XmlSchemaComplexType;
import org.apache.ws.commons.schema.XmlSchemaGroupRef;

/**
 *
 * @author lali
 */
public class HtmlComponentElementBuilder extends ComponentElementBuilder {

    private static final QName HTML_TAGS_GROUP = new QName("html-tags");
    
    public HtmlComponentElementBuilder(AttributeBuilderFactory attributeBuilderFactory, BaseAttributeGroupMngr baseAttributeGroupMngr, DesignContext designContext) {
        super(attributeBuilderFactory, baseAttributeGroupMngr, designContext);
    }

    @Override
    protected XmlSchemaComplexType createElementType(Component component) {
        XmlSchemaComplexType type = super.createElementType(component);
        type.setMixed(true);
        XmlSchemaGroupRef groupRef = new XmlSchemaGroupRef();
        groupRef.setRefName(HTML_TAGS_GROUP);
        type.setParticle(groupRef);
        return type;
    }

}
