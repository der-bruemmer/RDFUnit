package org.aksw.rdfunit.io.reader;

import org.apache.jena.rdf.model.Model;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.assertTrue;

@RunWith(Parameterized.class)

public class RDFStreamReaderTest {

    private Model model;

    @Before
    public void setUp() throws Exception {
        model = ReaderTestUtils.createOneTripleModel();
    }


    @Parameterized.Parameters(name= "{index}: file: {0}")
    public static Collection<Object[]> resources() throws Exception {

        String baseResDir = "/org/aksw/rdfunit/io/";
        return Arrays.asList(new Object[][] {
                { baseResDir + "onetriple.nq"},
                { baseResDir + "onetriple.nt"},
                { baseResDir + "onetriple.rdf"},
                { baseResDir + "onetriple.rj"},
                { baseResDir + "onetriple.trig"},
                { baseResDir + "onetriple.ttl"},
                //{ baseResDir + "onetriple.html"},
        });
    }

    @Parameterized.Parameter
    public String resourceName;



    @Test
    public void testReader() throws Exception{
        Model readModel = RDFReaderFactory.createResourceReader(resourceName).read();
        assertTrue("Models not the same", model.isIsomorphicWith(readModel));

    }




}