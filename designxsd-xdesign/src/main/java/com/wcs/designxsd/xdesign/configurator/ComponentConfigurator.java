package com.wcs.designxsd.xdesign.configurator;

import com.vaadin.ui.Component;
import java.util.Map;

/**
 *
 * @author lali
 */
public interface ComponentConfigurator {

    boolean isApplicable(Class<? extends Component> componentClass);

    void configurate(Component component, Map<String,String> customAttributes);
}
