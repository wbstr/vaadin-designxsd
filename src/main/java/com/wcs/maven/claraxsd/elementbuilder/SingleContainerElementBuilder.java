/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.wcs.maven.claraxsd.elementbuilder;

import com.wcs.maven.claraxsd.attributebuilder.AttributeBuilderFactory;
import com.wcs.maven.claraxsd.baseattributegroup.BaseAttributeGroupMngr;

/**
 *
 * @author kumm
 */
public class SingleContainerElementBuilder extends ContainerElementBuilder {

    public SingleContainerElementBuilder(AttributeBuilderFactory attributeBuilderFactory, BaseAttributeGroupMngr baseAttributeGroupMngr) {
        super(attributeBuilderFactory, baseAttributeGroupMngr);
    }

    @Override
    protected long getChildMaxOccurs() {
        return 1;
    }

}
