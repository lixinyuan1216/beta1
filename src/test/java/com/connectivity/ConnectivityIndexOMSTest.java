package com.connectivity;

import com.data.component.JsonParser;
import com.utilities.GeotoolsAssert;
import org.junit.Test;


import java.io.IOException;

/**
 * Created by xy on 11/05/16.
 */
public class ConnectivityIndexOMSTest {

    /**
     * // * Tests if the OMS wrapper works when called from Java (doesn't test the actual OMS annotations) //
     *
     * @throws IOException
     */
    @Test
    public void testConnectivityOMSWrapper() throws IOException {
        final String regionsFile = "src/test/testData/nei.json";
        final String networkFile = "src/test/testData/network.json";
        final String testFile = "src/test/testData/connectivity.json";

        ConnectivityIndexOMS connectivityOMS = new ConnectivityIndexOMS();
        JsonParser jp = new JsonParser();
        jp.readJSONFIle(networkFile);
        connectivityOMS.setNetwork(jp.getSource());
        jp.readJSONFIle(regionsFile);
        connectivityOMS.setRegions(jp.getSource());
        jp.readJSONFIle(testFile);
        connectivityOMS.run();
        GeotoolsAssert.assertFeatureSourceEquals(connectivityOMS.getResults(), jp.getSource());
    }

}
