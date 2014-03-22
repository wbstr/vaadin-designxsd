package com.wcs.wcslib.vaadin.claraxsd;

/*
 * Copyright 2001-2005 The Apache Software Foundation.
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
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.AbstractField;
import com.vaadin.ui.Component;
import com.vaadin.ui.ComponentContainer;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;

import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.namespace.QName;
import javax.xml.transform.stream.StreamSource;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.ws.commons.schema.XmlSchema;
import org.apache.ws.commons.schema.XmlSchemaAttribute;
import org.apache.ws.commons.schema.XmlSchemaAttributeGroup;
import org.apache.ws.commons.schema.XmlSchemaAttributeGroupRef;
import org.apache.ws.commons.schema.XmlSchemaCollection;
import org.apache.ws.commons.schema.XmlSchemaComplexType;
import org.apache.ws.commons.schema.XmlSchemaElement;
import org.apache.ws.commons.schema.XmlSchemaGroup;
import org.apache.ws.commons.schema.XmlSchemaGroupBase;
import org.apache.ws.commons.schema.XmlSchemaObjectCollection;
import org.apache.ws.commons.schema.XmlSchemaObjectTable;
import org.apache.ws.commons.schema.XmlSchemaType;
import org.reflections.ReflectionUtils;
import org.reflections.Reflections;
import org.reflections.util.ConfigurationBuilder;

/**
 * Goal which touches a timestamp file.
 *
 */
@Mojo(name = "trial", defaultPhase = LifecyclePhase.GENERATE_RESOURCES,
        requiresDependencyResolution = ResolutionScope.COMPILE)
public class ClaraXsdMojo
        extends AbstractMojo {

    @Parameter(property = "project.compileClasspathElements", required = true, readonly = true)
    private List<String> classpath;

    @Override
    public void execute()
            throws MojoExecutionException {

        Set<URL> urls = new HashSet<>();
        try {
            for (String element : classpath) {
                urls.add(new File(element).toURI().toURL());
            }
        } catch (MalformedURLException ex) {
            Logger.getLogger(ClaraXsdMojo.class.getName()).log(Level.SEVERE, null, ex);
        }

        ClassLoader contextClassLoader = URLClassLoader.newInstance(urls.toArray(new URL[0]));

        ConfigurationBuilder config = new ConfigurationBuilder()
                .addUrls(urls)
                .addClassLoader(contextClassLoader);
        Reflections reflections = new Reflections(config);
        Set<Class<? extends Component>> subTypesOf = reflections.getSubTypesOf(Component.class);
        XmlSchemaHandler xmlSchemaHandler = new XmlSchemaHandler();

        for (Class<? extends Component> componentClass : subTypesOf) {
            System.out.println("++" + componentClass.getCanonicalName());
            if (Modifier.isAbstract(componentClass.getModifiers())
                    || Modifier.isInterface(componentClass.getModifiers())
                    || !Modifier.isPublic(componentClass.getModifiers())) {
                continue;
            };
            if (componentClass.isMemberClass() && !Modifier.isStatic(componentClass.getModifiers())) {
                continue;
            }
            try {
                Constructor<? extends Component> constructor = componentClass.getConstructor(new Class<?>[]{});
                if (constructor == null || !Modifier.isPublic(constructor.getModifiers())) {
                    continue;
                }
            } catch (NoSuchMethodException e) {
                continue;
            }

            xmlSchemaHandler.addElement(componentClass);

//            ReflectionUtils.getAllMethods(componentClass, ReflectionUtils.withModifier(Modifier.ABSTRACT));
//            if (componentClass.)
        }
        xmlSchemaHandler.dump();
    }

    static class XmlSchemaHandler {

        private XmlSchemaObjectCollection allComponentsGroupItems;
        private XmlSchema schema;
        private XmlSchemaObjectTable elements;
        private List<KnownAttributeGroup> knownAttributeGroups = new ArrayList<>();

        public XmlSchemaHandler() {
            XmlSchemaCollection schemaCol = new XmlSchemaCollection();
            schemaCol.read(
                    new StreamSource(getClass().getResourceAsStream("clara-parent.xsd")), null);
            schema = schemaCol.read(
                    new StreamSource(getClass().getResourceAsStream("clara.xsd")), null);
            elements = schema.getElements();
            XmlSchemaGroup allComponentsGroup = (XmlSchemaGroup) schema.getGroups().getItem(
                    new QName("urn:import:com.vaadin.ui", "AllComponentsGroup"));
            XmlSchemaGroupBase allComponentsGroupParticle = allComponentsGroup.getParticle();
            allComponentsGroupItems = allComponentsGroupParticle.getItems();

            knownAttributeGroups.add(new KnownAttributeGroup(AbstractField.class));
            knownAttributeGroups.add(new KnownAttributeGroup(AbstractComponent.class));
            knownAttributeGroups.add(new KnownAttributeGroup(Component.class));
        }

        public void addElement(Class componentClass) {
            System.out.println("*** " + componentClass.getCanonicalName());
            XmlSchemaElement element = new XmlSchemaElement();
            element.setName(componentClass.getSimpleName());
            XmlSchemaComplexType type = new XmlSchemaComplexType(schema);
            element.setType(type);
            KnownAttributeGroup attributeGroup = findAttributeGroup(componentClass);
            type.getAttributes().add(attributeGroup.newRef());
            if (ComponentContainer.class.isAssignableFrom(componentClass)) {

            }
            elements.add(new QName("urn:import:com.vaadin.ui", element.getName()), element);
            /*
             Set<Method> allMethods = ReflectionUtils.getAllMethods(componentClass,
             ReflectionUtils.withModifier(Modifier.PUBLIC),
             ReflectionUtils.withPrefix("set")
             );*/
        }

        private KnownAttributeGroup findAttributeGroup(Class componentClass) {
            for (KnownAttributeGroup knownAttributeGroup : knownAttributeGroups) {
                if (knownAttributeGroup.isAppliesTo(componentClass)) {
                    return knownAttributeGroup;
                }
            }
            return null;
        }

        public void dump() {
            schema.write(System.out);
        }

        class KnownAttributeGroup {

            private Class parentClass;
            private List<String> attributes = new ArrayList<>();
            private String groupName;

            public KnownAttributeGroup(Class parentClass) {
                this.parentClass = parentClass;
                groupName = parentClass.getSimpleName() + "AttributeGroup";
                XmlSchemaAttributeGroup attrGroup = (XmlSchemaAttributeGroup) schema.getAttributeGroups().getItem(
                        new QName("urn:import:com.vaadin.ui", groupName));
                System.out.println("----- "+groupName+"-"+attrGroup);
                for (Iterator it = attrGroup.getAttributes().getIterator(); it.hasNext();) {
                    XmlSchemaAttribute attribute = (XmlSchemaAttribute) it.next();
                    attributes.add(attribute.getName());
                }
            }

            public boolean isAppliesTo(Class componentClass) {
                return parentClass.isAssignableFrom(componentClass);
            }

            public boolean hasAttribute(String attribute) {
                return attributes.contains(attribute);
            }

            public XmlSchemaAttributeGroupRef newRef() {
                XmlSchemaAttributeGroupRef ref = new XmlSchemaAttributeGroupRef();
                ref.setRefName(new QName(null, groupName, "v"));
                return ref;
            }

        }
    }

}
