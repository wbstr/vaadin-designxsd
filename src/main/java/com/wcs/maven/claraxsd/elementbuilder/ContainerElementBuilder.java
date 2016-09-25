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
import com.vaadin.ui.declarative.DesignContext;
import com.wcs.maven.claraxsd.attributebuilder.AttributeBuilderFactory;
import com.wcs.maven.claraxsd.baseattributegroup.BaseAttributeGroupMngr;
import org.apache.ws.commons.schema.XmlSchema;
import org.apache.ws.commons.schema.XmlSchemaComplexType;
import org.apache.ws.commons.schema.XmlSchemaElement;
import org.apache.ws.commons.schema.XmlSchemaGroupRef;

import javax.xml.namespace.QName;

/**
 *
 * @author kumm
 */
public class ContainerElementBuilder extends ComponentElementBuilder {

    public ContainerElementBuilder(AttributeBuilderFactory attributeBuilderFactory, BaseAttributeGroupMngr baseAttributeGroupMngr, DesignContext designContext) {
        super(attributeBuilderFactory, baseAttributeGroupMngr, designContext);
    }

    @Override
    public XmlSchemaElement buildElement(XmlSchema schema, Class<? extends Component> componentClass) {
        XmlSchemaElement element = super.buildElement(schema, componentClass);
        XmlSchemaComplexType type = (XmlSchemaComplexType) element.getSchemaType();
        type.setParticle(newAllGroupRef(schema));
        return element;
    }
    
    protected long getChildMaxOccurs() {
        return Long.MAX_VALUE;
    }
    
    private XmlSchemaGroupRef newAllGroupRef(XmlSchema schema) {
        XmlSchemaGroupRef allGroupRef = new XmlSchemaGroupRef();
        allGroupRef.setMinOccurs(0);
        allGroupRef.setMaxOccurs(getChildMaxOccurs());
        allGroupRef.setRefName(new QName(schema.getTargetNamespace(), "AllComponentsGroup"));
        return allGroupRef;
    }
    
}
