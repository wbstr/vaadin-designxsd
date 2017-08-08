package com.wcs.designxsd.xdesign.configurator;

import com.wcs.designxsd.xdesign.XDesign;
import org.junit.Test;

/**
 *
 * @author lali
 */
public class GridConfiguratorTest {

    @Test
    public void testConfigurate() {
        PersonComponent personComponent = new PersonComponent();
        XDesign.readAndConfigurate(personComponent, (model, propertyName) -> {
            return model.getName() + "." + propertyName;
        });
    }

}
