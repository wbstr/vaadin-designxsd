/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wcs.maven.claraxsd.itest;

import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.wcs.maven.claraxsd.GeneratedSchema;
import com.wcs.maven.claraxsd.Generator;
import com.wcs.maven.claraxsd.NamingRules;
import static com.wcs.maven.claraxsd.NamingRules.getGeneratedXsdSystemId;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.util.Collection;
import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import static org.junit.Assert.*;
import org.junit.Test;
import org.w3c.dom.bootstrap.DOMImplementationRegistry;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSInput;
import org.w3c.dom.ls.LSResourceResolver;

/**
 *
 * @author kumm
 */
public class GeneratorIntegrationTest {

    @Test
    public void testGenerate() throws Exception {
        Generator generator = Generator.create();
        generator.generate(Label.class);
        generator.generate(VerticalLayout.class);
        generator.generate(MyComponent.class);
        
        Collection<GeneratedSchema> generatedSchemas = generator.getGeneratedSchemas();
        assertEquals(2, generatedSchemas.size());

        SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        Validator validator = factory.newSchema().newValidator();
        validator.setResourceResolver(new MyResourceResolver(generatedSchemas));
        validator.validate(new StreamSource(getClass().getResourceAsStream("demo-layout.xml")));
    }

    private static class MyResourceResolver implements LSResourceResolver {
        
        final Collection<GeneratedSchema> generatedSchemas;
        final DOMImplementationLS domImplementation;

        public MyResourceResolver(Collection<GeneratedSchema> generatedSchemas) throws Exception {
            this.generatedSchemas = generatedSchemas;
            domImplementation = (DOMImplementationLS) DOMImplementationRegistry.newInstance()
                    .getDOMImplementation("LS");
        }
        
        @Override
        public LSInput resolveResource(String type, String namespaceURI, String publicId, String systemId, String baseURI) {
            LSInput input = domImplementation.createLSInput();
            input.setBaseURI(baseURI);
            input.setSystemId(systemId);
            input.setPublicId(publicId);
            for (NamingRules.FixedName fixed : NamingRules.FixedName.values()) {
                if (fixed.getSystemId().equals(systemId)) {
                    InputStream resourceAsStream = Generator.class.getResourceAsStream(fixed.getFileName());
                    input.setByteStream(resourceAsStream);
                    return input;
                }
            }
            for (GeneratedSchema generatedSchema : generatedSchemas) {
                if (getGeneratedXsdSystemId(generatedSchema.getComponentPackage()).equals(systemId)) {
                    ByteArrayOutputStream xsdBytes = new ByteArrayOutputStream();
                    generatedSchema.write(new OutputStreamWriter(xsdBytes));
                    input.setByteStream(new ByteArrayInputStream(xsdBytes.toByteArray()));
                    return input;
                }
            }
            throw new IllegalArgumentException("Unknown systemId: "+systemId);
        }
    }

}
