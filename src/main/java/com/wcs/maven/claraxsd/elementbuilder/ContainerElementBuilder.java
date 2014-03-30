/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.wcs.maven.claraxsd.elementbuilder;

import com.wcs.maven.claraxsd.attributebuilder.AttributeBuilderFactory;
import com.wcs.maven.claraxsd.baseattributegroup.BaseAttributeGroupMngr;
import javax.xml.namespace.QName;
import org.apache.ws.commons.schema.XmlSchema;
import org.apache.ws.commons.schema.XmlSchemaComplexType;
import org.apache.ws.commons.schema.XmlSchemaElement;
import org.apache.ws.commons.schema.XmlSchemaGroupRef;

/**
 *
 * @author kumm
 */
public class ContainerElementBuilder extends ComponentElementBuilder {

    public ContainerElementBuilder(AttributeBuilderFactory attributeBuilderFactory, BaseAttributeGroupMngr baseAttributeGroupMngr) {
        super(attributeBuilderFactory, baseAttributeGroupMngr);
    }

    @Override
    public XmlSchemaElement buildElement(XmlSchema schema, Class componentClass) {
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
