package com.statistic;

import oms3.annotations.*;
import org.geotools.data.DataUtilities;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.feature.FeatureIterator;
import org.opengis.feature.simple.SimpleFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

/**
 * Created by xy on 11/05/16.
 */
@Name("zscore")
@Description("For a given list of attributes and a set of features, calculates the z score for each attribute and sums the z scores")
public class ZScoreOMS {

    static final Logger LOGGER = LoggerFactory.getLogger(ZScoreOMS.class);
    /**
     * Attributes to calculate z-score for
     */
    @In
    @Name("Zscore attributes")
    @Description("Attributes to calculate z-score for")
    public List<String> attributes;

    /**
     * Regions of interest (GeoJSON)
     */
    @In
    @Name("Neighbourhoods")
    public SimpleFeatureSource regionsSource;
    /**
     * Resulting regions with z-scores for each attribute and sum of z-scores across attributes
     */
    @Out
    @Name("Result regions")
    @Description("Resulting regions with z-scores for each attribute and sum of z-scores across attributes")
    public SimpleFeatureSource resultsSource;

    /**
     * For a given list of attributes and a set of features, calculates the z score for each attribute and sums the z
     * scores Reads in the regions layer from given URL, writes out results to resultsURL
     */
    @Execute
    public void sumOfZScores() {
        try {
            LOGGER.info("Calculating Z-Score");
            FeatureIterator<SimpleFeature> regions = regionsSource.getFeatures().features();

            SimpleFeatureCollection statisticsRegions = ZScore.sumZScores(regions, attributes);

            resultsSource = DataUtilities.source(statisticsRegions);
            LOGGER.info("Completed Z-Score calculation");
        } catch (IOException e) {
            LOGGER.error("Failed to read input/s");
        }
    }
}
