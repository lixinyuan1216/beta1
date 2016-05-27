package com.data.component;

import com.data.component.JsonParser;
import org.junit.Test;
import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;

import static org.junit.Assert.assertEquals;
/**
 * Created by xy on 10/05/16.
 */
public class JsonParserTest {
    JsonParser jp;

    @Test
    public void testReader() throws Exception {
        final String path = "src/test/testData/points.json";

        jp = new JsonParser();
        jp.readJSONFIle(path);
        //JsonWriter writer = new JsonWriter();
        //writer.features = jp.getSource();
        //System.out.println(writer.writeFeaturesToTempFile().toString());
        int size = jp.getSource().getFeatures().size();
        assertEquals(10, size);
    }
}
