# claraxsd-maven-plugin

This is a maven plugin to generate xml schema files for [Vaadin Clara addon](https://vaadin.com/directory#addon/clara:vaadin).

The main goal of the project is to generate xml schemas to make code completion, and validation possible for clara xml templates. The generated schemas should work in any IDE with OASIS XML catalog support and schema based code completion support. As far as i know this means Eclipse, and Intellij IDEA too. Currently tested only in netbeans.

## What the plugin do

Scans your whole project classpath (compile + system + provided maven scopes), and generates schema for any Vaadin component it founds, and supported by Clara.

In practice this means you get xml schema: 

1. for standard vaadin components (com.vaadin.ui package)

2. for components defined in vaadin addons used by your project

3. for components defined in your project.

### Generated files

The plugin generates the following files:

1. clara_parent.xsd
Xml schema xsd for 'urn:vaadin:parent' namespace. 
Contains componentAlignment, expandRatio, and position attributes.

2. clara_base.xsd
Xml schema for base vaadin component classes. Defines attribute groups for most common component base classes. For example com.vaadin.ui.Component, com.vaadin.ui.AbstractComponent, com.vaadin.ui.AbstractField.

3. clara-[java-package-name].xsd
One xsd file per java package with vaadin component.
All the elements in these schemas reference to the corresponding base attribute group from the previous section.
The target namespace for the schema is "urn:import:[java-package-name]"

4. catalog.xml
The plugin also generates an OASIS XML Catalog. This catalog assings a unique systemId (an url) to the generated xsd files. You can reference this systemId at schemaLocation in your clara xml template files. This is the file you have to import in your ide setting somewhere.

## Plugin documentation

The plugin binds to generate-resources maven phase by default. The only goal is 'generate'.

### generate goal options

##### baseSystemId

This is the base url for the systemId of the schemas.
Default: "clara://${project.groupId}:${project.artifactId}:${project.version}/"

##### destination

Desintation direactory path for generated files. Plugin writes all (schemas, and catalog) files to this directory.
Default: "${project.build.directory}/claraxsd"


## How to use it

### install the maven plugin
Clone the project from this github repository, enter it's directoy, and run a maven install.
```
git clone https://github.com/kumm/claraxsd-maven-plugin.git
cd claraxsd-maven-plugin
mvn clean install
```

### generate schemas

Go to your web project's directory, and run 
```
mvn com.wcs.maven:claraxsd-maven-plugin:1.0-SNAPSHOT:generate -D baseSystemId="clara://myproject"
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
urn:import:com.vaadin.ui clara://myproject/com.vaadin.ui.xsd
urn:import:com.wcs.maven.claraxsd.itest clara://myproject/com.myproject.component.xsd
"
    margin="true"
    spacing="true">

    <Label id="label" contentMode="HTML" value="Welcome to &lt;strong&gt;Clara&lt;/strong&gt; demo application." />

    <a:MyComponent aProp="str" bProp="42" cProp="" l:componentAlignment="TOP_LEFT"/>

</VerticalLayout>
```

It's necessary to define namespaces, and schameLocation in every clara xml. This is the way how xml works. If you are smart enough, you can solve it by defining a template in your IDE for clara files.

### Inform your IDE about the generated schemas

The exact manner to do depends on your IDE. Currently i can share the netbeans howto only.

#### Netbeans

1. Go to 'Tools/DTDs and XML Schemas' menu.

2. Click 'Add Catalog', select 'OASIS Catalog Resolver', and browse the generated catalog.xml

After this step it's ready. Code completion, and validation should work while editing clara xml template.
