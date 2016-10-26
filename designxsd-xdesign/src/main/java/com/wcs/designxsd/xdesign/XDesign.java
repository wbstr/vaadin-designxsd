package com.wcs.designxsd.xdesign;

import com.vaadin.ui.AbstractOrderedLayout;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Component;
import com.vaadin.ui.HasComponents;
import com.vaadin.ui.declarative.Design;
import com.vaadin.ui.declarative.DesignAttributeHandler;
import com.vaadin.ui.declarative.DesignContext;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Map;
import org.jsoup.nodes.Attributes;

/**
 *
 * @author lali
 */
public class XDesign {

    private static final String EXPAND_ATTRIBUTE_NAME = "_expand";
    private static final String ALIGN_MIDDLE = "_middle";
    private static final String ALIGN_BOTTOM = "_bottom";
    private static final String ALIGN_CENTER = "_center";
    private static final String ALIGN_RIGHT = "_right";

    public static DesignContext read(Component rootComponent) {
        DesignContext designContext = Design.read(rootComponent);
        processCustomAttributes(designContext);
        return designContext;
    }
    
    public static DesignContext read(String filename, Component rootComponent) {
        DesignContext designContext = Design.read(filename, rootComponent);
        processCustomAttributes(designContext);
        return designContext;
    }
    
    public static DesignContext read(InputStream stream, Component rootComponent) {
        DesignContext designContext = Design.read(stream, rootComponent);
        processCustomAttributes(designContext);
        return designContext;
    }

    public static Component read(InputStream design) {
        DesignContext designContext = read(design, null);
        processCustomAttributes(designContext);
        return designContext.getRootComponent();
    }
    
    private static void processCustomAttributes(DesignContext designContext) {
        Component rootComponent = designContext.getRootComponent();
        stepNext(rootComponent, designContext);
    }

    private static void stepNext(Component component, DesignContext designContext) {
        if (component instanceof HasComponents) {
            HasComponents hc = (HasComponents) component;
            Iterator<Component> iterator = hc.iterator();
            while (iterator.hasNext()) {
                Component child = iterator.next();
                processCustomAttributes(child, designContext);
                stepNext(child, designContext);
            }
        }
    }

    private static void processCustomAttributes(Component c, DesignContext designContext) {
        Map<String, String> customAttribute = designContext.getCustomAttributes(c);
        if (customAttribute == null) {
            return;
        }

        Attributes attributes = new Attributes();
        for (Map.Entry<String, String> entry : customAttribute.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();

            switch (key) {
                case EXPAND_ATTRIBUTE_NAME:
                    processExpand(value, c);
                    break;
                case ALIGN_MIDDLE:
                case ALIGN_BOTTOM:
                case ALIGN_CENTER:
                case ALIGN_RIGHT:
                    attributes.put(key, value);
                    break;
            }
        }

        processAlignment(attributes, c);
    }

    private static void processExpand(String value, Component c) {
        float ratio = Float.valueOf(value);
        HasComponents parent = c.getParent();
        if (parent instanceof AbstractOrderedLayout) {
            AbstractOrderedLayout layout = (AbstractOrderedLayout) parent;
            layout.setExpandRatio(c, ratio);
        } else {
            throw new IllegalArgumentException(
                    "Component must be added to layout before using " + EXPAND_ATTRIBUTE_NAME + " attribute.");
        }
    }

    private static void processAlignment(Attributes attributes, Component c) {
        Alignment alignment = DesignAttributeHandler.readAlignment(attributes);

        HasComponents parent = c.getParent();
        if (parent instanceof AbstractOrderedLayout) {
            AbstractOrderedLayout layout = (AbstractOrderedLayout) parent;
            layout.setComponentAlignment(c, alignment);
        } else {
            throw new IllegalArgumentException(
                    "Component must be added to layout before using alignment attributes.");
        }
    }
}
