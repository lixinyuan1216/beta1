package com.projection;
import oms3.annotations.Execute;
import oms3.annotations.In;
import oms3.annotations.Name;
import oms3.annotations.Out;

import org.geotools.feature.FeatureCollection;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.geometry.MismatchedDimensionException;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.referencing.operation.TransformException;

import au.org.aurin.gis.service.AuSpatialReference;
import au.org.aurin.gis.service.SrsHandlerService;
/**
 * Created by xy on 26/05/16.
 */
@Name("lookupGrid")
public class LookupComponent {

    @In
    public FeatureCollection<SimpleFeatureType, SimpleFeature> featureCollection;

    @Out
    public AuSpatialReference epsgOut;

    @Execute
    public void run() throws MismatchedDimensionException,
            NoSuchAuthorityCodeException, FactoryException, TransformException {
        SrsHandlerService service = new SrsHandlerService();
        epsgOut = service.lookupGrid(featureCollection);
    }

}
