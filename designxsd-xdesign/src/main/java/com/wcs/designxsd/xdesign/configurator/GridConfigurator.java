package com.wcs.designxsd.xdesign.configurator;

import com.vaadin.ui.Component;
import com.vaadin.ui.Grid;
import java.util.Map;

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
        configurateWithModel(component, modelClassName);
    }

    private void configurateWithModel(Component component, String modelClassName) {
        if (modelClassName == null) {
            System.out.println("Couldn't find model type");
            return;
        }

        System.out.println("Component: " + component.getClass());
        System.out.println("Model class name: " + modelClassName);
    }
}
