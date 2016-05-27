package com.utilities;

import com.vividsolutions.jts.geom.Geometry;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.data.simple.SimpleFeatureSource;
import org.junit.Assert;
import org.opengis.feature.Property;
import org.opengis.feature.simple.SimpleFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by xy on 11/05/16.
 */
public class GeotoolsAssert {

    public static final double DOUBLE_COMPAR_PREC = 0.00001;
    private static final int GEOMETRY_PRECISION = 1000; // TODO: improve accuracy
    static final Logger LOGGER = LoggerFactory.getLogger(GeotoolsAssert.class);

    private GeotoolsAssert() {
    }

    /**
     * Compares two SimpleFeatureSource and throws exceptions if they are not roughly equivalent (features with the same
     * id have the same attributes
     *
     * @param expected
     * @param actual
     * @throws IOException
     */
    public static void assertFeatureSourceEquals(SimpleFeatureSource expected, SimpleFeatureSource actual)
            throws IOException {
        SimpleFeatureIterator iteratorExpected = expected.getFeatures().features();
        SimpleFeatureIterator iteratorActual = actual.getFeatures().features();
        Map<String, SimpleFeature> mapA = new HashMap<String, SimpleFeature>();
        Assert.assertTrue(expected.getFeatures().size() == actual.getFeatures().size());
        try {
            while (iteratorExpected.hasNext()) {
                SimpleFeature feature = iteratorExpected.next();
                mapA.put(feature.getID(), feature);
            }
            while (iteratorActual.hasNext()) {
                SimpleFeature actualFeature = iteratorActual.next();
                SimpleFeature expectedFeature = mapA.get(actualFeature.getID());
                if (expectedFeature == null) {
                    Assert.fail("Result does not contain feature with ID: " + actualFeature.getID());
                }
                // Test Geometry area equivalence
                //System.out.println("Geometry Name: " + expectedFeature.getDefaultGeometryProperty().getName());
                Geometry geomB = (Geometry) (actualFeature.getDefaultGeometry());
                Geometry geomA = (Geometry) (expectedFeature.getDefaultGeometry());
                Long areaAround = Math.round(geomB.getArea() / GEOMETRY_PRECISION);
                Long areaBround = Math.round(geomA.getArea() / GEOMETRY_PRECISION);
                Assert.assertEquals("Geometry Areas not equal", areaAround, areaBround, 1);

                LOGGER.debug("Area A: " + areaAround + " Area B: " + areaBround);

                // Test properties (excluding geometry) equivalence --> A can be a
                // *subset* of B, comparison is not symmetrical
                for (Property p : expectedFeature.getProperties()) {
                    if (p.getName() != expectedFeature.getDefaultGeometryProperty().getName()) {
                        Object valueB = actualFeature.getProperty(p.getName()).getValue();
                        Object valueA = p.getValue();
                        LOGGER.debug("A {}, B {}", valueA, valueB);

                        if (Double.class.isAssignableFrom(valueA.getClass())) {
                            Assert.assertEquals("Feature property of type double " + p.getName() + " values not equal",
                                    (Double) valueA, (Double) valueB, DOUBLE_COMPAR_PREC);
                        } else {
                            Assert.assertEquals("Feature property " + p.getName() + " values not equal", valueA.toString(),
                                    valueB.toString());
                        }
                    }
                }
            }
        } finally {
            iteratorExpected.close();
            iteratorActual.close();
        }
    }
}
