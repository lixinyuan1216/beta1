package com.statistic;

import com.utilities.ValidationUtils;
import org.apache.commons.math.stat.descriptive.SummaryStatistics;
import org.geotools.data.DataUtilities;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Calculates the z-score and sum of z-scores for regions of interest and a list of attributes
 * Created by xy on 11/05/16.
 */
public class ZScore {

    static final Logger LOGGER = LoggerFactory.getLogger(ZScore.class);

    private ZScore() {
    }

    /**
     * Calculates z-score and sum of z-scores
     *
     * @param features
     *          Regions of interest
     * @param attributes
     *          Attributes to calculate z-scores for
     * @return
     */
    public static SimpleFeatureCollection sumZScores(FeatureIterator<SimpleFeature> features, List<String> attributes) {
        List<SimpleFeature> results = new ArrayList<>();

        Map<String, SummaryStatistics> stats = new HashMap<>();
        for (String attr : attributes) {
            SummaryStatistics s = new SummaryStatistics();
            stats.put(attr, s);
        }
        try {
            // Build up summary statistics for each attribute across all
            // features
            while (features.hasNext()) {
                SimpleFeature region = features.next();
                for (String attr : attributes) {
                    final Object attribute = region.getAttribute(attr);
                    if (attribute == null) {
                        throw new IllegalStateException("Cannot compute sum of Zscores. Attribute '" + attr +  "' was not produced by the previous stage");
                    }
                    stats.get(attr).addValue((Double) attribute);
                }
                results.add(buildFeature(region, attributes));
            }
        } finally {
            features.close();
        }

        // Calculate Z-Score for each attribute in each feature and also sum the
        // z-scores for the set of attributes
        for (SimpleFeature region : results) {
            double totalZ = 0.0;
            for (String attr : attributes) {
                double rawScore = (Double) region.getAttribute(attr);
                double zScore = (rawScore - stats.get(attr).getMean()) / stats.get(attr).getStandardDeviation();
                if (!ValidationUtils.isValidDouble(zScore)) {
                    region.setAttribute(attr + "_ZScore", null);
                } else {
                    region.setAttribute(attr + "_ZScore", zScore);
                    totalZ += zScore;
                }
                if (attributes.size() > 1) {
                    region.setAttribute("SumZScore", ValidationUtils.isValidDouble(totalZ) ? totalZ : null);
                }
            }
            LOGGER.debug("Z-score: {}", totalZ);
        }

        return DataUtilities.collection(results);
    }

    private static SimpleFeature buildFeature(SimpleFeature region, List<String> attributes) {

        SimpleFeatureType sft = (SimpleFeatureType) region.getType();
        SimpleFeatureTypeBuilder stb = new SimpleFeatureTypeBuilder();
        stb.init(sft);
        stb.setName("statisticsFeatureType");
        for (String attr : attributes) {
            stb.add(attr + "_ZScore", Double.class);
        }
        if (attributes.size() > 1) {
            stb.add("SumZScore", Double.class);
        }
        SimpleFeatureType statsFT = stb.buildFeatureType();
        SimpleFeatureBuilder sfb = new SimpleFeatureBuilder(statsFT);
        sfb.addAll(region.getAttributes());

        return sfb.buildFeature(region.getID());

    }
}
