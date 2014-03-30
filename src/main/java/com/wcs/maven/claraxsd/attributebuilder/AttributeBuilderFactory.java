/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wcs.maven.claraxsd.attributebuilder;

import java.util.Arrays;
import java.util.Collection;

public class AttributeBuilderFactory {

    Collection<AttributeBuilder> builders;
    private NopAttributeBuilder nopAttributeBuilder;

    public AttributeBuilderFactory() {
        nopAttributeBuilder = new NopAttributeBuilder();
        builders = Arrays.asList(
                new PrimitiveAttributeBuilder(),
                new EnumAttributeBuilder(),
                new NoValueAttributeBuilder()
        );
    }

    public AttributeBuilder getAttributeBuilder(String name, Class<?> type) {
        if ("debugId".equals(name)) {
            return nopAttributeBuilder;
        }
        for (AttributeBuilder builder : builders) {
            if (builder.isSupports(type)) {
                return builder;
            }
        }
        return nopAttributeBuilder;
    }

}
