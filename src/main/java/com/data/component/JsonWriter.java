package com.data.component;

/**
 * Created by xy on 12/05/16.
 */
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import lombok.extern.slf4j.Slf4j;
import oms3.annotations.*;

import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.geojson.feature.FeatureJSON;
import org.geotools.geojson.geom.GeometryJSON;


@Name("aurin_geojson_writer")
@Description("Stores features as GeoJSON")
@Slf4j
public class JsonWriter {

    @In
    public SimpleFeatureSource features;

    private boolean hasGeometry = false;

    public File writeFeaturesToTempFile() throws IOException,
            FileNotFoundException {

        final FeatureJSON featureJSON = new FeatureJSON(new GeometryJSON(15));
        featureJSON.setEncodeNullValues(true);
        if (hasGeometry) {
            featureJSON.setEncodeFeatureCollectionBounds(true);
            featureJSON.setEncodeFeatureCollectionCRS(true);
        }

        final File temp = File.createTempFile("geojson_writer", ".json");

        OutputStream outputStream = null;

        try {
            outputStream = new BufferedOutputStream(new FileOutputStream(temp));
            featureJSON.writeFeatureCollection(features.getFeatures(), outputStream);
        } finally {
            if (outputStream != null) {
                outputStream.close();
            }
        }
        return temp;
    }
}