/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.wcs.maven.claraxsd.itest;

import com.vaadin.ui.Label;
import com.wcs.maven.claraxsd.GeneratedSchema;
import com.wcs.maven.claraxsd.Generator;
import java.io.OutputStreamWriter;
import java.util.Collection;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author kumm
 */
//TODO: really test something
public class GenerateIntegrationTest {
    
    @Test
    public void testGenerate() {
        Generator generator = Generator.create();
        generator.generate(Label.class);
        generator.generate(MyComponent.class);
        
        Collection<GeneratedSchema> generatedSchemas = generator.getGeneratedSchemas();
        
        assertEquals(2, generatedSchemas.size());

        for (GeneratedSchema generatedSchema : generatedSchemas) {
            System.out.println("*** "+generatedSchema.getComponentPackage());
            generatedSchema.write(new OutputStreamWriter(System.out));
        }
    }
    
}
