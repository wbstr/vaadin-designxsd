package com.wcs.designxsd.xdesign;

import com.vaadin.ui.AbstractOrderedLayout;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Component;
import com.vaadin.ui.HasComponents;
import com.vaadin.ui.declarative.Design;
import com.vaadin.ui.declarative.DesignAttributeHandler;
import com.vaadin.ui.declarative.DesignContext;
import java.util.Iterator;
import java.util.Map;
import org.jsoup.nodes.Attributes;

/**
 *
 * @author lali
 */
public class XDesign {

    private static final String EXPAND_ATTRIBUTE_NAME = "_expand";
    private static final String ALIGNMENT_ATTRIBUTE_NAME = "_align";
    private static final String ALIGN_SEPARATOR = "_";

    public static DesignContext read(Component component) {
        DesignContext designContext = Design.read(component);
        processCustomAttributes(designContext);
        return designContext;
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

    public static void processCustomAttributes(Component c, DesignContext designContext) {
        Map<String, String> customAttribute = designContext.getCustomAttributes(c);
        if (customAttribute == null) {
            return;
        }

        for (Map.Entry<String, String> entry : customAttribute.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();

            switch (key) {
                case EXPAND_ATTRIBUTE_NAME:
                    processExpand(value, c);
                    break;
                case ALIGNMENT_ATTRIBUTE_NAME:
                    processAlignment(value, c);
                    break;
            }
        }
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

    private static void processAlignment(String value, Component c) {
        HasComponents parent = c.getParent();
        if (parent instanceof AbstractOrderedLayout) {
            String horizontalAlign = value.substring(0, value.indexOf(ALIGN_SEPARATOR));
            String verticalAlign = value.substring(value.indexOf(ALIGN_SEPARATOR) + 1);
            Attributes attr = new Attributes();
            attr.put(":" + horizontalAlign, "");
            attr.put(":" + verticalAlign, "");
            Alignment alignment = DesignAttributeHandler.readAlignment(attr);

            AbstractOrderedLayout layout = (AbstractOrderedLayout) parent;
            layout.setComponentAlignment(c, alignment);
        } else {
            throw new IllegalArgumentException(
                    "Component must be added to layout before using " + ALIGNMENT_ATTRIBUTE_NAME + " attribute.");
        }
    }
}
