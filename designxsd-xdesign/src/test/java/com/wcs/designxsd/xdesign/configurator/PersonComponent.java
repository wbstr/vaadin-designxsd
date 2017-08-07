package com.wcs.designxsd.xdesign.configurator;

import com.vaadin.annotations.DesignRoot;
import com.vaadin.ui.Grid;
import com.vaadin.ui.VerticalLayout;
import com.wcs.designxsd.xdesign.XDesign;

/**
 *
 * @author lali
 */
@DesignRoot
public class PersonComponent extends VerticalLayout {

    Grid<Person> xmlGrid;
    
    public PersonComponent() {
        XDesign.read(this);
    }
    
}
