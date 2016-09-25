package com.wcs.maven.designxsd;

import com.vaadin.ui.Component;
import com.vaadin.ui.Grid;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.declarative.DesignAttributeHandler;
import com.vaadin.ui.declarative.DesignContext;
import com.vaadin.ui.declarative.DesignFormatter;
import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Tag;
import org.junit.Test;
import org.mockito.Mockito;

import java.lang.reflect.Field;
import java.util.*;

/**
 * Created by kumm on 2016.09.23..
 */
public class DesignTest {
    @Test
    public void testTrial() throws Exception {
        Component component = new VerticalLayout();
        Collection<String> supportedAttributes = DesignAttributeHandler.getSupportedAttributes(component.getClass());
        Field formatterField = DesignAttributeHandler.class.getDeclaredField("FORMATTER");
        formatterField.setAccessible(true);
        StubDesignFormatter designFormatter = new StubDesignFormatter();
        formatterField.set(null, designFormatter);

        Tag tag = Mockito.mock(Tag.class);

        StubAttributes attributes = new StubAttributes();
        Element design = new Element(tag, "", attributes);
        for (String attribute : supportedAttributes) {
            design.attributes().put(attribute, attribute);
        }
        DesignContext designContext = new DesignContext();
        component.readDesign(design, designContext);

        HashSet<String> customAttributes = new HashSet<>(attributes.checked);
        customAttributes.removeAll(supportedAttributes);

        for (String s : customAttributes) {
            design.attributes().put(s, s);
            try {
                component.readDesign(design, designContext);
            } catch (IllegalArgumentException ignore) {
            }
            design.attributes().remove(s);
        }
        System.out.println(designFormatter.parsed);

        HashSet<String> markerAttributes = new HashSet<>(attributes.checked);
        markerAttributes.removeAll(attributes.fetched);
        markerAttributes.removeAll(designFormatter.parsed.keySet());

        System.out.println(markerAttributes);

        HashSet<String> specialAttributes = new HashSet<>(attributes.fetched);
        specialAttributes.removeAll(designFormatter.parsed.keySet());
        System.out.println(specialAttributes);
    }
}

class StubDesignFormatter extends DesignFormatter {
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
            return (T) new String();
        }
        if (type.isEnum()) {
            return type.getEnumConstants()[0];
        }
        return null;
    }
}

class StubAttributes extends Attributes {
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

