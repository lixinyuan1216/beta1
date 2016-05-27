package com.connectivity;

import au.org.aurin.gis.service.SrsHandlerService;
import com.data.component.JsonParser;

import com.projection.ProjectToGridComponent;
import com.utilities.GeoJSONUtilities;
import org.geotools.data.DataUtilities;
import org.junit.Test;
import com.utilities.GeotoolsAssert;

import java.net.URL;


/**
 * Created by xy on 11/05/16.
 */
public class NetworkBufferTest {

    SrsHandlerService srsHandlerService = new SrsHandlerService();

    @Test
    public void testNetworkBufferOMS() throws Exception {
        final String networkFile = "src/test/testData/psma_cut_projected.geojson";
        final String pointsFile = "src/test/testData/Rndm5ptsProjected.json";
        final String testFile = "src/test/testData/networkBufferOMS.geojson";
        JsonParser jp = new JsonParser();

        NetworkBufferOMS networkBufferOMS = new NetworkBufferOMS();
        jp.readJSONFIle(networkFile);
        networkBufferOMS.setNetwork(jp.getSource());
        jp.readJSONFIle(pointsFile);
        networkBufferOMS.setPoints(jp.getSource());
        networkBufferOMS.setBufferSize(100.0);
        networkBufferOMS.setDistance(1600.0);
        networkBufferOMS.run();
        jp.readJSONFIle(testFile);
        GeotoolsAssert.assertFeatureSourceEquals(networkBufferOMS.getRegions(),jp.getSource());
    }


    @Test
    public void test() throws Exception {
        final String networkFile = "src/test/testData/network.json";
        final String pointsFile = "src/test/testData/points.json";
        final String testFile = "src/test/testData/nei.json";
        JsonParser jp = new JsonParser();

        NetworkBufferOMS networkBufferOMS = new NetworkBufferOMS();
        jp.readJSONFIle(networkFile);
        networkBufferOMS.setNetwork(jp.getSource());
        jp.readJSONFIle(pointsFile);
        networkBufferOMS.setPoints(jp.getSource());
        networkBufferOMS.setBufferSize(100.0);
        networkBufferOMS.setDistance(1600.0);
        networkBufferOMS.run();
        jp.readJSONFIle(testFile);
        GeotoolsAssert.assertFeatureSourceEquals(networkBufferOMS.getRegions(),jp.getSource());
    }
}


