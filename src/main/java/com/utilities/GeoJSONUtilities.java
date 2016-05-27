package com.utilities;

import org.apache.commons.compress.compressors.CompressorException;
import org.apache.commons.compress.compressors.CompressorInputStream;
import org.apache.commons.compress.compressors.CompressorStreamFactory;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.geotools.geojson.feature.FeatureJSON;
import org.opengis.feature.simple.SimpleFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * Handles reading and writing of GeoJSON in and out of Geotools
 * Created by xy on 11/05/16.
 */
public class GeoJSONUtilities {

    static final Logger LOGGER = LoggerFactory.getLogger(GeoJSONUtilities.class);

    private GeoJSONUtilities() {
    }

    /**
     * Writes out a SimpleFeatureCollection to a file as geojson
     *
     * @param features
     *          The features to write
     * @param file
     *          The file to write to
     */
    public static void writeFeatures(SimpleFeatureCollection features, File file) {
        FeatureJSON fjson = new FeatureJSON();
        OutputStream os;
        try {
            os = new FileOutputStream(file);
            try {
                if (features.getSchema().getCoordinateReferenceSystem() != null) {
                    fjson.setEncodeFeatureCollectionBounds(true);
                    fjson.setEncodeFeatureCollectionCRS(true);
                } else {
                    LOGGER.debug("CRS is null");
                }
                fjson.writeFeatureCollection(features, os);
            } finally {
                os.close();
            }
        } catch (FileNotFoundException e1) {
            LOGGER.error("Failed to write feature collection " + e1.getMessage());
        } catch (IOException e) {
            LOGGER.error("Failed to write feature collection " + e.getMessage());
        }
    }

    /**
     * Writes a SimpleFeatureCollection to a URL as geojson
     *
     * @param features
     *          The features to write out
     * @param dataStoreURL
     *          The URL to write to (will overwrite existing)
     */
    public static URL writeFeatures(SimpleFeatureCollection features, URL dataStoreURL) {
        String dataStore = dataStoreURL.toString();

        try {
            LOGGER.debug("Writing to File resource {}", dataStore);
            writeFeatures(features, new File(dataStoreURL.toURI()));
            return dataStoreURL;
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
        return null; // FIXME: add proper error handling
    }

    /**
     * Writes a single feature to file
     *
     * @param feature
     * @param file
     */
    public static void writeFeature(SimpleFeature feature, File file) {
        FeatureJSON fjson = new FeatureJSON();
        try {
            OutputStream os = new FileOutputStream(file);
            try {
                fjson.writeFeature(feature, os);
            } finally {
                os.close();
            }
        } catch (IOException e) {
            LOGGER.error("Failed to write feature collection" + e.getMessage());
        }
    }

    /**
     * Reads a single feature from GeoJSON
     *
     * @param url
     *          A URL pointing to a GeoJSON feature
     * @return The feature from the URL
     * @throws IOException
     */
    public static SimpleFeature readFeature(URL url) throws IOException {
        FeatureJSON io = new FeatureJSON();
        return io.readFeature(url.openConnection().getInputStream());
    }

    /**
     * Gets a FeatureIterator from a GeoJSON URL, does not need to read all the features?
     *
     * @param url
     *          The FeatureCollection URL
     * @return An Iterator for the features at the URL
     * @throws IOException
     */
    public static FeatureIterator<SimpleFeature> getFeatureIterator(URL url) throws IOException {
        LOGGER.debug("Reading features from URL {}", url);
        FeatureJSON io = new FeatureJSON();
        io.setEncodeFeatureCollectionCRS(true);
        return io.streamFeatureCollection(url.openConnection().getInputStream());
    }

    /**
     * Gets a SimpleFeatureCollection from a GeoJSON URL - reads all the features
     *
     * @param url
     *          The FeatureCollection URL
     * @return The features at the URL
     * @throws IOException
     */
    public static SimpleFeatureCollection readFeatures(URL url) throws IOException {
        FeatureJSON io = new FeatureJSON();

        io.setEncodeFeatureCRS(true);
        FeatureIterator<SimpleFeature> features = null;
        try {
            if (url.toString().contains(".geojson.gz")) {
                CompressorInputStream input;

                input = new CompressorStreamFactory().createCompressorInputStream(new BufferedInputStream(new FileInputStream(
                        new File(url.toURI()))));
                return (SimpleFeatureCollection) io.readFeatureCollection(input);

            }
            return (SimpleFeatureCollection) io.readFeatureCollection(url.openConnection().getInputStream());
        } catch (CompressorException e) {
            throw new IOException(e);
        } catch (URISyntaxException e) {
            throw new IOException(e);
        }
    }
}