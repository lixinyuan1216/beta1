package com.connectivity;

import com.data.component.JsonParser;
import com.utilities.GeotoolsAssert;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.junit.Test;
import org.junit.experimental.theories.suppliers.TestedOn;


import javax.jms.*;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Created by xy on 11/05/16.
 */
public class ConnectivityIndexOMSTest {

    /**
     * // * Tests if the OMS wrapper works when called from Java (doesn't test the actual OMS annotations) //
     *
     * @throws IOException
     */
   /* @Test
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
/*

    @Test
    public void testAll() throws IOException {

        long startTime = System.currentTimeMillis();
        final String pointsFile = "src/test/testData/Rndm5ptsProjected.json";
        final String networkFile = "src/test/testData/psma_cut_projected.geojson";
        final String testFile = "src/test/testData/connectivityOMSTest.geojson";

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
        jp.readJSONFIle(testFile);
        connectivityOMS.run();
        System.out.print(System.currentTimeMillis() - startTime);
        //GeotoolsAssert.assertFeatureSourceEquals(connectivityOMS.getResults(), jp.getSource());
    }
*/

    @Test
    public void testReceiver() throws IOException{
        ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory
                ("tcp://115.146.93.32:61616");

        Connection connection = null;
        try {
            connection = factory.createConnection();
            connection.start();

            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

            Queue original_queue = session.createQueue("original_queue?consumer.prefetchSize=1");
            MessageConsumer consumer = session.createConsumer(original_queue);

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
                messages++;
                PrintWriter writer = new PrintWriter(messages + ".txt", "UTF-8");
                writer.println(consumeMessage.getText());
                writer.close();

                final String pointsFile = messages + ".txt";

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

            } while (true);

        } catch (JMSException e) {
            e.printStackTrace();
        }


        //System.exit(0);
    }
}

