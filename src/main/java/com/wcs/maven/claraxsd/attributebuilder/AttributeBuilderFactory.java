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
package com.wcs.maven.claraxsd.attributebuilder;

import java.util.Arrays;
import java.util.Collection;

public class AttributeBuilderFactory {

    Collection<AttributeBuilder> builders;
    private final NopAttributeBuilder nopAttributeBuilder;

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
