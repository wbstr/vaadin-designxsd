package com.wcs.designxsd.xdesign;

import com.vaadin.annotations.DesignRoot;
import com.vaadin.ui.AbstractOrderedLayout;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Component;
import com.vaadin.ui.ComponentRootSetter;
import com.vaadin.ui.Composite;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.HasComponents;
import com.vaadin.ui.declarative.Design;
import com.vaadin.ui.declarative.DesignAttributeHandler;
import com.vaadin.ui.declarative.DesignContext;
import com.vaadin.ui.declarative.DesignException;
import com.vaadin.ui.declarative.FieldBinder;
import com.wcs.designxsd.xdesign.configurator.GridConfigurator;
import java.beans.IntrospectionException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;

/**
 *
 * @author lali
 */
public class XDesign {

    private static final String X_PARENT_PREFIX = "_";
    private static final String V_PARENT_PREFIX = ":";
    private static final String EXPAND_ATTRIBUTE_NAME = X_PARENT_PREFIX + "expand";
    private static final String ALIGN_MIDDLE = X_PARENT_PREFIX + "middle";
    private static final String ALIGN_BOTTOM = X_PARENT_PREFIX + "bottom";
    private static final String ALIGN_CENTER = X_PARENT_PREFIX + "center";
    private static final String ALIGN_RIGHT = X_PARENT_PREFIX + "right";

    public static DesignContext readAndConfigurate(Component componentRoot, CaptionGenerator captionGenerator) {
        InputStream xDesign = XDesign.readXDesignFile(componentRoot);

        Document doc;
        try {
            doc = Jsoup.parse(xDesign, "UTF-8", "", Parser.htmlParser());
        } catch (IOException ex) {
            throw new DesignException("The html document cannot be parsed.");
        }

        StructureDesignContext structureDesignContext = new StructureDesignContext();
        structureDesignContext.readPackageMappings(doc);

        Element root = doc.body();
        Elements children = root.children();
        if (children.size() != 1) {
            throw new DesignException(
                    "The first level of a component hierarchy should contain at most one root component, but found "
                    + children.size() + ".");
        }
        Element element = children.first();

        FieldBinder binder;
        try {
            binder = new FieldBinder(componentRoot);
        } catch (IntrospectionException e) {
            throw new DesignException(
                    "Could not bind fields of the root component", e);
        }

        DesignContext.ComponentCreationListener creationListener = (
                DesignContext.ComponentCreatedEvent event) -> {
            binder.bindField(event.getComponent(), event.getLocalId());
        };
        structureDesignContext.addComponentCreationListener(creationListener);

        if (componentRoot instanceof CustomComponent
                || componentRoot instanceof Composite) {
            Component rootComponent = structureDesignContext.readDesign(element);
            ComponentRootSetter.setRoot(componentRoot, rootComponent);
        } else {
            structureDesignContext.readDesign(element, componentRoot);
        }
        // make sure that all the member fields are bound
        Collection<String> unboundFields = binder.getUnboundFields();
        if (!unboundFields.isEmpty()) {
            throw new DesignException(
                    "Found unbound fields from component root "
                    + unboundFields);
        }
        // no need to listen anymore
        structureDesignContext.removeComponentCreationListener(creationListener);

        GridConfigurator gridConfigurator = new GridConfigurator(captionGenerator);
        for (Component component : structureDesignContext.getGeneratedComponents()) {
            if (gridConfigurator.isApplicable(component.getClass())) {
                Map<String, String> customAttributes = structureDesignContext.getCustomAttributes(component);
                gridConfigurator.configurate(component, customAttributes);
            }
        }

        return structureDesignContext;
    }

    public static DesignContext read(Component rootComponent) {
        InputStream xDesignFile = readXDesignFile(rootComponent);
        DesignContext designContext = Design.read(xDesignFile, rootComponent);
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

    public static InputStream readXDesignFile(Component rootComponent) throws IllegalArgumentException, DesignException {
        Class<? extends Component> annotatedClass = findClassWithAnnotation(
                rootComponent.getClass(), DesignRoot.class);
        if (annotatedClass == null) {
            throw new IllegalArgumentException(
                    "The class "
                    + rootComponent.getClass().getName()
                    + " or any of its superclasses do not have an @DesignRoot annotation");
        }

        DesignRoot designAnnotation = annotatedClass
                .getAnnotation(DesignRoot.class);
        String filename = designAnnotation.value();
        if (filename.equals("")) {
            // No value, assume the html file is named as the class
            filename = annotatedClass.getSimpleName() + ".xml";
        }
        InputStream stream = annotatedClass.getResourceAsStream(filename);
        if (stream == null) {
            throw new DesignException("Unable to find design file " + filename
                    + " in " + annotatedClass.getPackage().getName());
        }

        return stream;
    }

    @FunctionalInterface
    public interface CaptionGenerator {

        String generateCaption(Class<?> modelClass, String propertyName);

    }

    private static Class<? extends Component> findClassWithAnnotation(
            Class<? extends Component> componentClass,
            Class<? extends Annotation> annotationClass) {
        if (componentClass == null) {
            return null;
        }

        if (componentClass.isAnnotationPresent(annotationClass)) {
            return componentClass;
        }

        Class<?> superClass = componentClass.getSuperclass();
        if (!Component.class.isAssignableFrom(superClass)) {
            return null;
        }

        return findClassWithAnnotation(superClass.asSubclass(Component.class),
                annotationClass);
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
                    key = key.replace(X_PARENT_PREFIX, V_PARENT_PREFIX);
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
        }
    }

    private static class StructureDesignContext extends DesignContext {

        private final Set<Component> generatedComponents = new HashSet<>();

        @Override
        public boolean setComponentLocalId(Component component, String localId) {
            generatedComponents.add(component);
            return super.setComponentLocalId(component, localId);
        }

        @Override
        public void readPackageMappings(Document doc) {
            super.readPackageMappings(doc);
        }

        public Set<Component> getGeneratedComponents() {
            return Collections.unmodifiableSet(generatedComponents);
        }

    }
}
