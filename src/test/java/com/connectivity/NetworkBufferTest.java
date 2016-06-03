package com.connectivity;

import au.org.aurin.gis.service.SrsHandlerService;
import com.data.component.JsonParser;

import org.junit.Test;
import com.utilities.GeotoolsAssert;


/**
 * Created by xy on 11/05/16.
 */
public class NetworkBufferTest {

    //projection service
    //SrsHandlerService srsHandlerService = new SrsHandlerService();

    /*
    Test neighbourhood generator
     */
   /* @Test
    public void testNetworkBufferOMS() throws Exception {
        final String networkFile = "src/test/testData/psma_cut_projected.geojson";
        final String pointsFile = "src/test/testData/Rndm5ptsProjected.json";
        final String testFile = "src/test/testData/networkBufferOMS.geojson";
        JsonParser jp = new JsonParser();

        //set the value for street information
        NetworkBufferOMS networkBufferOMS = new NetworkBufferOMS();
        jp.readJSONFIle(networkFile);
        networkBufferOMS.setNetwork(jp.getSource());

        //set the value for points information
        jp.readJSONFIle(pointsFile);
        networkBufferOMS.setPoints(jp.getSource());

        //user defined parameters
        networkBufferOMS.setBufferSize(100.0);
        networkBufferOMS.setDistance(1600.0);

        //run the computation
        networkBufferOMS.run();
        jp.readJSONFIle(testFile);

        //compare actual results and actual results
        GeotoolsAssert.assertFeatureSourceEquals(networkBufferOMS.getRegions(),jp.getSource());
    }*/

}


