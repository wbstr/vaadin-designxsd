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
package com.wcs.maven.designxsd.discoverer;

import com.vaadin.ui.Component;
import com.vaadin.ui.declarative.DesignAttributeHandler;
import com.vaadin.ui.declarative.DesignContext;
import com.vaadin.ui.declarative.DesignFormatter;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;
import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Tag;

/**
 *
 * @author lali
 */
public class AttributeDiscoverer {

    private Component component;
    private Collection<String> supportedAttributes;
    private StubDesignFormatter designFormatter;
    private StubAttributes attributes;

    public Map<String, Class> discovery(Component component) {
        this.component = component;
        this.supportedAttributes = DesignAttributeHandler.getSupportedAttributes(component.getClass());
        this.designFormatter = new StubDesignFormatter();
        this.attributes = new StubAttributes();

        spyDesignAttributeHandler();
        Element stubDesign = readStubDesign();
        Map<String, Class> collectedAttributes = collectAttributes(stubDesign);
        collectedAttributes.remove("tabindex");
        return collectedAttributes;
    }

    private void spyDesignAttributeHandler() {
        try {
            Field formatterField = DesignAttributeHandler.class.getDeclaredField("FORMATTER");
            formatterField.setAccessible(true);

            Field modifiersField = Field.class.getDeclaredField("modifiers");
            modifiersField.setAccessible(true);
            modifiersField.setInt(formatterField, formatterField.getModifiers() & ~Modifier.FINAL);

            formatterField.set(null, designFormatter);
        } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException ex) {
            throw new RuntimeException(ex);
        }
    }

    private Element readStubDesign() {
        Tag tag = Tag.valueOf(component.getClass().getSimpleName());
        Element stubDesign = new Element(tag, "", attributes);
        for (String attribute : supportedAttributes) {
            stubDesign.attributes().put(attribute, attribute);
        }

        readDesignWithoutLogger(stubDesign);

        return stubDesign;
    }

    private Map<String, Class> collectAttributes(Element stubDesign) {
        HashSet<String> customAttributes = new HashSet<>(attributes.checked);
        customAttributes.removeAll(supportedAttributes);

        for (String s : customAttributes) {
            stubDesign.attributes().put(s, s);

            readDesignWithoutLogger(stubDesign);

            stubDesign.attributes().remove(s);
        }

        Map<String, Class> discoveredAttributes = new HashMap<>();
        discoveredAttributes.putAll(designFormatter.parsed);

        HashSet<String> markerAttributes = new HashSet<>(attributes.checked);
        markerAttributes.removeAll(attributes.fetched);
        markerAttributes.removeAll(designFormatter.parsed.keySet());
        markerAttributes.forEach(key -> discoveredAttributes.put(key, null));

        HashSet<String> specialAttributes = new HashSet<>(attributes.fetched);
        specialAttributes.removeAll(designFormatter.parsed.keySet());
        specialAttributes.forEach(key -> discoveredAttributes.put(key, null));

        return discoveredAttributes;
    }

    private void readDesignWithoutLogger(Element stubDesign) {
        Logger logger = Logger.getLogger(DesignAttributeHandler.class.getName());
        logger.setUseParentHandlers(false);
        try {
            component.readDesign(stubDesign, new DesignContext());
        } catch (Exception ignore) {
        }
        logger.setUseParentHandlers(true);
    }

    private class StubDesignFormatter extends DesignFormatter {

        Map<String, Class> parsed = new HashMap<>();

        @Override
        public <T> T parse(String value, Class<? extends T> type) {
            parsed.put(value, type);//esetleg valueOf("") viszi mind
            if (Boolean.class.equals(type) || boolean.class.equals(type)) {
                return (T) Boolean.FALSE;
            }
            if (Integer.class.equals(type) || int.class.equals(type)) {
                return (T) new Integer(0);
            }
            if (Double.class.equals(type) || double.class.equals(type)) {
                return (T) new Double(0);
            }
            if (String.class.equals(type)) {
                return (T) new String(value);
            }
            if (type.isEnum()) {
                return type.getEnumConstants()[0];
            }
            return null;
        }
    }

    private class StubAttributes extends Attributes {

        Set<String> checked = new HashSet<>();
        Set<String> fetched = new HashSet<>();

        @Override
        public boolean hasKey(String key) {
            checked.add(key);
            return super.hasKey(key);
        }

        @Override
        public String get(String key) {
            fetched.add(key);
            return super.get(key);
        }
    }
}
