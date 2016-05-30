package com.data.component;

/**
 * Created by xy on 10/05/16.
 */
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;

import au.org.aurin.gis.service.SrsHandlerService;
import lombok.extern.slf4j.Slf4j;
import oms3.annotations.Description;
import oms3.annotations.Execute;
import oms3.annotations.Label;
import oms3.annotations.Name;
import oms3.annotations.Out;

import org.geotools.data.DataUtilities;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.geojson.feature.FeatureJSON;
import org.geotools.geojson.geom.GeometryJSON;
import org.opengis.feature.simple.SimpleFeatureType;

import au.org.aurin.workflow.annotations.AURINWFConstants;

@Name("geojson_reader")
@Label(AURINWFConstants.DATA_PRE_PROCESSING)
@Description("Reads GeoJSON from a file and produces a SimpleFeatureSource")
@Slf4j
public class JsonParser{

    @Out
    @Name("Features")
    @Description("Resulting feature read from a GeoJSON service")
    private SimpleFeatureSource source;

    public JsonParser(SimpleFeatureSource source) {
        this.source = source;
    }

    public JsonParser() {
    }

    @Execute
    public void readJSONFIle(String path) throws MalformedURLException, IOException {
        InputStream is = null;
        try {
            // parse JSON into FeatureCollection
            File geoJsonfile = new File(path);
            is = new FileInputStream(geoJsonfile);

            FeatureJSON featureJSON = new FeatureJSON(new GeometryJSON(15));

            SimpleFeatureType type = featureJSON.readFeatureCollectionSchema(is, false);
            featureJSON.setFeatureType(type);

            is = new FileInputStream(geoJsonfile);

            SimpleFeatureCollection featureCollection = (SimpleFeatureCollection) featureJSON
                    .readFeatureCollection(is);

            source = DataUtilities.source(featureCollection);
        }
        catch(Exception e){
            System.out.print("error");
        }
        finally{
            is.close();
        }
    }

    public SimpleFeatureSource getSource() {
        return source;
    }

    public void setSource(SimpleFeatureSource source) {
        this.source = source;
    }
}
