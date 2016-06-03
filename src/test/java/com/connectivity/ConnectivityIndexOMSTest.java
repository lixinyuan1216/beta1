package com.connectivity;

import com.data.component.JsonParser;
import com.utilities.GeotoolsAssert;
import com.vividsolutions.jts.geom.Geometry;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.data.simple.SimpleFeatureSource;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.junit.Test;
import org.opengis.feature.Property;
import org.opengis.feature.simple.SimpleFeature;


import javax.jms.*;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.UUID;

/**
 * Created by xy on 11/05/16.
 */
public class ConnectivityIndexOMSTest {

    /**
     * // * Tests if the OMS wrapper works when called from Java (doesn't test the actual OMS annotations) //
     *
     * @throws IOException
     */
/*    @Test
    public void testConnectivityOMSWrapper() throws IOException {
        final String regionsFile = "src/test/testData/networkBufferOMS.geojson";
        final String networkFile = "src/test/testData/psma_cut_projected.geojson";
        final String testFile = "src/test/testData/connectivityOMSTest.geojson";

        ConnectivityIndexOMS connectivityOMS = new ConnectivityIndexOMS();
        JsonParser jp = new JsonParser();
        jp.readJSONFIle(networkFile);
        connectivityOMS.setNetwork(jp.getSource());
        jp.readJSONFIle(regionsFile);
        connectivityOMS.setRegions(jp.getSource());
        jp.readJSONFIle(testFile);
        connectivityOMS.run();
        GeotoolsAssert.assertFeatureSourceEquals(connectivityOMS.getResults(), jp.getSource());
    }*/


    //Run walkability computation in single VM
    @Test
    public void testAll() throws IOException {

        final String pointsFile = "src/test/testData/Rndm5ptsProjected.json";
        final String networkFile = "src/test/testData/psma_cut_projected.geojson";
        //expected results
        //final String testFile = "src/test/testData/connectivityOMSTest.geojson";

        //initial file parser
        JsonParser jp = new JsonParser();

        NetworkBufferOMS networkBufferOMS = new NetworkBufferOMS();
        jp.readJSONFIle(networkFile);
        networkBufferOMS.setNetwork(jp.getSource());
        jp.readJSONFIle(pointsFile);
        networkBufferOMS.setPoints(jp.getSource());
        networkBufferOMS.setBufferSize(100.0);
        networkBufferOMS.setDistance(1600.0);
        networkBufferOMS.run();

        //initial connectivity calculator
        ConnectivityIndexOMS connectivityOMS = new ConnectivityIndexOMS();

        jp.readJSONFIle(networkFile);
        connectivityOMS.setNetwork(jp.getSource());
        connectivityOMS.setRegions(networkBufferOMS.getRegions());
        //jp.readJSONFIle(testFile);
        connectivityOMS.run();


        SimpleFeatureIterator iterator = connectivityOMS.getResults().getFeatures().features();
        while(iterator.hasNext()){
            SimpleFeature feature = iterator.next();
            for (Property p : feature.getProperties()) {
                System.out.print(p.getName() + " : " + p.getValue().toString() + " | ");
            }
            System.out.println();
        }
        //System.out.print(System.currentTimeMillis() - startTime);
        //GeotoolsAssert.assertFeatureSourceEquals(connectivityOMS.getResults(), jp.getSource());
    }

    //Master program block
    /*@Test
    public void testSender() throws IOException{
        ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory
                ("tcp://localhost:61616");

        Connection connection = null;
        try {
            connection = factory.createConnection();
            connection.start();
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

            // Create a reference to the queue test_queue in this session. Note
            //  that ActiveMQ has auto-creation enabled by default, so this JMS
            //  destination will be created on the broker automatically
            Queue new_queue = session.createQueue("new_queue");
            MessageConsumer consumer = session.createConsumer(new_queue);

            Queue original_queue = session.createQueue("original_queue");
            MessageProducer producer = session.createProducer(original_queue);
            TextMessage sendMessage;

            // Create a simple text message and send it

            JSONParser parser = new JSONParser();

            Object obj = parser.parse(new FileReader("src/test/testData/Rndm5ptsProjected.json"));

            JSONObject jsonObject = (JSONObject) obj;

            JSONArray pointsList = (JSONArray) jsonObject.get("features");

            for(int i = 0; i < 30; i++){
                JSONObject newObj = new JSONObject();
                JSONArray newArray = new JSONArray();
                newArray.add(pointsList.get(i));

                newObj.put("features", newArray.toString());

                newObj.put("type", "FeatureCollection");

                sendMessage = session.createTextMessage (newObj.toJSONString().replaceAll("\\\\","").replaceAll("\"\\[","[").replaceAll("\\]\"","\\]"));
                producer.send(sendMessage);
            }
            int i = 0;
            while(i < 30){
                i++;
                consumer.receive();
            }
            connection.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    //slave program block
    @Test
    public void testReceiver() throws IOException{
        ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory
                ("tcp://115.146.93.32:61616");

        Connection connection = null;
        try {
            connection = factory.createConnection();
            connection.start();

            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

            //the point queue, prefetch is 1
            //take one point from the queue at each time
            Queue original_queue = session.createQueue("original_queue?consumer.prefetchSize=1");
            MessageConsumer consumer = session.createConsumer(original_queue);

            //the result queue
            Queue new_queue = session.createQueue("new_queue");
            MessageProducer producer = session.createProducer(new_queue);

            TextMessage consumeMessage;
            TextMessage sendMessage;
            int messages = 0;
            String mes;
            JsonParser jp = new JsonParser();
            NetworkBufferOMS networkBufferOMS = new NetworkBufferOMS();
            ConnectivityIndexOMS connectivityOMS = new ConnectivityIndexOMS();
            final String networkFile = "src/test/testData/psma_cut_projected.geojson";
            jp.readJSONFIle(networkFile);
            networkBufferOMS.setNetwork(jp.getSource());
            networkBufferOMS.setBufferSize(100.0);
            networkBufferOMS.setDistance(1600.0);
            connectivityOMS.setNetwork(jp.getSource());
            do
            {
                long startTime = System.currentTimeMillis();
                consumeMessage = (TextMessage)consumer.receive();
                UUID uuid = UUID.randomUUID();
                final String pointsFile = uuid.toString() + ".txt";
                PrintWriter writer = new PrintWriter(pointsFile, "UTF-8");
                writer.println(consumeMessage.getText());
                writer.close();

                //initial file parser
                //JsonParser jp = new JsonParser();
                //NetworkBufferOMS networkBufferOMS = new NetworkBufferOMS();

                jp.readJSONFIle(pointsFile);
                networkBufferOMS.setPoints(jp.getSource());
                networkBufferOMS.run();

                //initial connectivity calculator
                // ConnectivityIndexOMS connectivityOMS = new ConnectivityIndexOMS();

                connectivityOMS.setRegions(networkBufferOMS.getRegions());
                connectivityOMS.run();

                mes = connectivityOMS.getResults().toString();
                sendMessage = session.createTextMessage (mes);
                producer.send(sendMessage);
                System.out.println((System.currentTimeMillis() - startTime) + " is the elapsed time for message " + messages);
                messages++;

            } while (true);

        } catch (JMSException e) {
            e.printStackTrace();
        }
    }*/
}

