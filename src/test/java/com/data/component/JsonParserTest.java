package com.data.component;

import org.junit.Test;


import static org.junit.Assert.assertEquals;
/**
 * Created by xy on 10/05/16.
 */
public class JsonParserTest {
    JsonParser jp;

   @Test
    public void testReader() throws Exception {
        final String path = "src/test/testData/Rndm5ptsProjected.json";

        jp = new JsonParser();
        jp.readJSONFIle(path);

        int size = jp.getSource().getFeatures().size();
        assertEquals(10, size);

    }

    /*@Test
    public void testWriter() throws Exception {
        final String path = "src/test/testData/Rndm5ptsProjected.json";

        jp = new JsonParser();
        jp.readJSONFIle(path);
        *//*JsonWriter writer = new JsonWriter();
        writer.features = jp.getSource();
        System.out.println(writer.writeFeaturesToTempFile().toString());*//*
        int size = jp.getSource().getFeatures().size();
        assertEquals(10, size);
    }*/
}

