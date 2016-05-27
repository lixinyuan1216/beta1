package com.connectivity;

import oms3.annotations.*;
import org.geotools.data.DataUtilities;
import org.geotools.data.Query;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.data.store.ReprojectingFeatureCollection;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * An OMS Wrapper for Network Buffer generation
 * Created by xy on 11/05/16.
 */

@Name("netbuffer")
@Description("Generates neibourhood polygons as service areas for points on a network")
public class NetworkBufferOMS {

    static final Logger LOGGER = LoggerFactory.getLogger(NetworkBufferOMS.class);
    /**
     * The road network to count connections from
     */
    @In
    @Name("Road Network")
    @Description("The road network dataset to generate regions/service areas")
    private SimpleFeatureSource network;
    /**
     * The points of interest
     */
    @In
    @Name("Points")
    @Description("The input point dataset. The points are used as origins when calculating regions/service areas")
    private SimpleFeatureSource points;
    /**
     * The network distance for the service areas (maximum walk distance)
     */
    @In
    @Name("Maximum walk distance")
    @Description("The maximum distance to traverse the network in all possible directions")
    private Double distance;
    /**
     * The buffer size
     */
    @In
    @Name("Trim distance")
    @Description("Trim Regions/service areas extend the specified trim distance from the road network lines")
    private Double bufferSize;

    /**
     * The resulting regions url
     */
    @Out
    @Name("Resulting regions")
    private SimpleFeatureSource regions;

    @Out
    @Name("The original road network")
    private SimpleFeatureSource networkOut;

    public static Logger getLOGGER() {
        return LOGGER;
    }

    public SimpleFeatureSource getNetwork() {
        return network;
    }

    public void setNetwork(SimpleFeatureSource network) {
        this.network = network;
    }

    public SimpleFeatureSource getPoints() {
        return points;
    }

    public void setPoints(SimpleFeatureSource points) {
        this.points = points;
    }

    public Double getDistance() {
        return distance;
    }

    public void setDistance(Double distance) {
        this.distance = distance;
    }

    public Double getBufferSize() {
        return bufferSize;
    }

    public void setBufferSize(Double bufferSize) {
        this.bufferSize = bufferSize;
    }

    public SimpleFeatureSource getRegions() {
        return regions;
    }

    public void setRegions(SimpleFeatureSource regions) {
        this.regions = regions;
    }

    public SimpleFeatureSource getNetworkOut() {
        return networkOut;
    }

    public void setNetworkOut(SimpleFeatureSource networkOut) {
        this.networkOut = networkOut;
    }


    public NetworkBufferOMS() {
    }

    /**
     * Reads the input network and point datasets then uses NetworkBufferBatch to generate all the network buffers and
     * writes out to regions URL
     */



    @Execute
    public void run() {

        validateInputs();

        try {
            LOGGER.debug("Received network data containing {} features", network.getCount(new Query()));
            LOGGER.debug("Received points data containing {} features", points.getCount(new Query()));

            final CoordinateReferenceSystem pointsCRS = points.getSchema().getCoordinateReferenceSystem();
            LOGGER.debug("Points Source CRS: {}", pointsCRS);
            final CoordinateReferenceSystem networkCRS = network.getSchema().getCoordinateReferenceSystem();
            LOGGER.debug("Roads Source CRS: {}", networkCRS);

            SimpleFeatureCollection pointsFC = points.getFeatures();

            if (pointsCRS != null && !pointsCRS.equals(networkCRS)) {
                pointsFC = new ReprojectingFeatureCollection(pointsFC, networkCRS);
            }

            LOGGER.info("Generate network service areas...");
            NetworkBufferBatch nbb = new NetworkBufferBatch(network, pointsFC, distance, bufferSize);
            SimpleFeatureCollection buffers = nbb.createBuffers();

            if (buffers.isEmpty()) {
                throw new IllegalStateException("No buffers were generated. Aborting process");
            }

            // File file = new File("service_areas_oms.geojson");
            regions = DataUtilities.source(buffers);

            // regions = file.toURI().toURL();
            LOGGER.info("Completed Network Service Area Generation");

            networkOut = network;

        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            throw new IllegalStateException(e);
        }
    }

    private void validateInputs() {
        System.out.println(network.getSchema().getCoordinateReferenceSystem().getCoordinateSystem().getAxis(0).getUnit().toString());

        if (network == null) {
            throw new IllegalArgumentException("Network buffer error: A road network was not provided");
        }

        if (points == null) {
            throw new IllegalArgumentException("Network buffer error: A set of points was not provided");
        }

        if (distance == null) {
            throw new IllegalArgumentException("Network buffer error: A walking distance must be provided");
        }

        if (bufferSize == null) {
            throw new IllegalArgumentException("Network buffer error: A buffer size must be provided");
        }

        if (network.getSchema().getCoordinateReferenceSystem() == null) {
            throw new IllegalArgumentException("Network dataset does not contain a CRS");
        }

        if (!network.getSchema().getCoordinateReferenceSystem().getCoordinateSystem().getAxis(0).getUnit().toString()
                .equals("m")) {
            throw new IllegalArgumentException("Network axis unit is not m");
        }

    }

    /*
     * Validate outputs
     */
    @Finalize
    public void validateOutputs() throws IOException {

        if (regions.getCount(new Query()) == 0) {
            throw new IllegalArgumentException(
                    "Cannot continue tool execution; no buffers were generated");
        }
    }
}
