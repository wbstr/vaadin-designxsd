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
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author lali
 */
public class OptionDiscoverer {

    Set<String> optionComponentNames = new HashSet<String>() {
        {
            add("com.vaadin.ui.OptionGroup");
            /*
            TODO
            1 Felsorolni a többit is, aminél van értelme az option tag-nek
            2 Mivel ez egy html tag így amik megjelennek az xsd-ben, azok valójában nem relevánsak számunkra
            Bővítsük ki a vaadinos attribútumokkal is és a felhasználóra bízzuk, hogy tudja melyik mikor értelmes?
            Más ötlet?            
            */
        }
    };

    public boolean discover(Component component) {
        return optionComponentNames.contains(component.getClass().getName());
    }

}
