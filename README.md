# claraxsd-maven-plugin

This is a maven plugin to generate xml schema files for [Vaadin Clara addon](https://vaadin.com/directory#addon/clara:vaadin).

The main goal of the project is to generate xml schemas to make code completion, and validation possible for clara xml templates. The generated schemas should work in any IDE with OASIS XML catalog support and schema based code completion support. As far as i know this means Eclipse, and Intellij IDEA too. Currently tested only in netbeans.

## What the plugin does

Scans your whole project classpath (compile + system + provided maven scopes), and generates schema for any Vaadin component it founds, and supported by Clara.
In practice it means you get xml schema: 

- for standard vaadin components (com.vaadin.ui package)
- for components defined in vaadin addons used by your project
- for components defined in your project.

### Generated files

The plugin generates the following files:

- clara_parent.xsd  
XML schema xsd for 'urn:vaadin:parent' namespace.  
Technically this file is not generated, it is just copied to destination.  
Contains componentAlignment, expandRatio, and position attributes.  
- clara_base.xsd  
XML schema for base vaadin component classes. Defines attribute groups for most common component base classes.  
Technically this file is not generated, it is just copied to destination.  
For example com.vaadin.ui.Component, com.vaadin.ui.AbstractComponent, com.vaadin.ui.AbstractField.
- clara-[java-package-name].xsd  
One xsd file per java package with vaadin component. 
All the elements in these schemas reference to the corresponding base attribute group from the previous section. 
The target namespace for the schema is "urn:import:[java-package-name]"
- catalog.xml  
The plugin also generates an OASIS XML Catalog. 
This catalog assigns a unique systemId (an url) to the generated xsd files. You can reference this systemId at schemaLocation in your clara xml template files.
This is the file you have to import in your ide settings somewhere.

## Plugin options

The plugin binds to generate-resources maven phase by default. The only goal is 'generate'.

### generate goal options

##### baseSystemId

This is the base url for the systemId of the schemas.  
Default: "http://${project.groupId}:${project.artifactId}:${project.version}/"

##### destination

Destination directory path for generated files. Plugin writes all (schemas, and catalog) files to this directory.
Default: "${project.build.directory}/claraxsd"

## How to use it

### install the maven plugin
Clone the project from this github repository, enter it's directory, and run a maven install.
```
git clone https://github.com/kumm/claraxsd-maven-plugin.git
cd claraxsd-maven-plugin
mvn clean install
```

### generate schemas

Go to your web project's directory, and run 
```
mvn com.wcs.maven:claraxsd-maven-plugin:1.0-SNAPSHOT:generate -D baseSystemId="http://myproject/"
```
'-D baseSystemId=...' is optional, but i think the default is too verbose in most cases. See generate goal options.
Or of course you can add the plugin to your pom.xml in plugins section...

### Set schemaLocations in your XML

For example:
```
<VerticalLayout
    xmlns="urn:import:com.vaadin.ui"
    xmlns:a="urn:import:com.myproject.component"
    xmlns:l="urn:vaadin:parent"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xsi:schemaLocation="
urn:import:com.vaadin.ui http://myproject/com.vaadin.ui.xsd
urn:import:com.myproject.component http://myproject/com.myproject.component.xsd
"
    margin="true"
    spacing="true">

    <Label id="label" contentMode="HTML" value="Welcome to &lt;strong&gt;Clara&lt;/strong&gt; demo application." />

    <a:MyComponent aProp="str" bProp="42" cProp="" l:componentAlignment="TOP_LEFT"/>

</VerticalLayout>
```

It's necessary to define namespaces, and schemaLocation in every clara xml. This is the way how xml works. If you are smart enough, you can solve it by defining a template in your IDE for clara files.

### Inform your IDE about the generated schemas

#### NetBeans

1. Go to 'Tools/DTDs and XML Schemas' menu.
2. Click 'Add Catalog', select 'OASIS Catalog Resolver', and browse the generated catalog.xml

#### Eclipse

Preferences > XML > XML Catalog > User Specified Entries > Add > Next Catalog

#### IntelliJ IDEA

According to [help](http://www.jetbrains.com/idea/webhelp/xml-catalog.html).

1. Create a property file like [this](http://xerces.apache.org/xml-commons/components/resolver/tips.html#properties)  
I found absolute path of the generated catalog.xml works only at "catalog" property.
2. Browse property file at File > Settings > Schemas and DTDs > XML Catalog
3. Exlude the claraxsd destination directory at Project Structure. By default it's in target, so it is excluded.

After this step it's ready. Code completion, and validation should work while editing clara xml template.
