package com.wcs.designxsd.xdesign.configurator;

import com.vaadin.data.BeanPropertySet;
import com.vaadin.data.PropertyDefinition;
import com.vaadin.data.PropertySet;
import com.vaadin.data.ValueProvider;
import com.vaadin.server.VaadinServiceClassLoaderUtil;
import com.vaadin.ui.Component;
import com.vaadin.ui.Grid;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 *
 * @author lali
 */
public class GridConfigurator implements ComponentConfigurator {

    private static final String MODEL_TYPE_ATTRIBUTE = "model-type";

    @Override
    public boolean isApplicable(Class<? extends Component> componentClass) {
        return componentClass.isAssignableFrom(Grid.class);
    }

    @Override
    public void configurate(Component component, Map<String, String> customAttributes) {
        if (!isApplicable(component.getClass())) {
            throw new IllegalArgumentException("Configurator not applicable on "
                    + component.getClass().getSimpleName()
                    + " component.");
        }

        String modelClassName = customAttributes.get(MODEL_TYPE_ATTRIBUTE);
        configurateWithModel((Grid) component, modelClassName);
    }

    private void configurateWithModel(Grid grid, String modelClassName) {
        if (modelClassName == null) {
            return;
        }

        Class<?> model = resolveClass(modelClassName);
        PropertySet<?> propertySet = BeanPropertySet.get(model);
        List<? extends PropertyDefinition<?, ?>> def = propertySet.getProperties().collect(Collectors.toList());
        for (PropertyDefinition<?, ?> propertyDefinition : def) {
            String name = propertyDefinition.getName();
            ValueProvider<?, ?> getter = propertyDefinition.getGetter();

            Grid.Column newColumn = grid.addColumn(getter);
            newColumn.setId(name);
            newColumn.setCaption(name); // TODO
        }
    }

    private Class<?> resolveClass(String qualifiedClassName) {
        try {
            Class<?> resolvedClass = Class.forName(qualifiedClassName, true,
                    VaadinServiceClassLoaderUtil.findDefaultClassLoader());
            return resolvedClass;
        } catch (ClassNotFoundException | SecurityException e) {
            throw new IllegalArgumentException(
                    "Unable to find class " + qualifiedClassName, e);
        }

    }
}
