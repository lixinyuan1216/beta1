package com.connectivity;

import com.data.component.JsonParser;
import com.utilities.GeotoolsAssert;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.junit.Test;
import org.junit.experimental.theories.suppliers.TestedOn;


import javax.jms.*;
import java.io.IOException;

/**
 * Created by xy on 11/05/16.
 */
public class ConnectivityIndexOMSTest {

    /**
     * // * Tests if the OMS wrapper works when called from Java (doesn't test the actual OMS annotations) //
     *
     * @throws IOException
     */
    @Test
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
    }

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

    @Test
    public void testPro() throws IOException{
        ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory
                ("tcp://localhost:61616");

        // Note that a new thread is created by createConnection, and it
        //  does not stop even if connection.stop() is called. We must
        //  shut down the JVM using System.exit() to end the program
        Connection connection = null;
        try {
            connection = factory.createConnection();
            // Start the connection
            connection.start();
            // Create a non-transactional session with automatic acknowledgement
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
            for(int i = 0; i < 5; i++){
                sendMessage = session.createTextMessage ("Hello, world! Number " + i);
                producer.send(sendMessage);
            }
            String mes;
            int i = 0;
            while(i < 5){
                i++;
                mes = ((TextMessage)consumer.receive()).getText();
                System.out.println(mes);
                //System.out.println(oriQueueSize.intValue() + "  " +newQueueSize.intValue());
                // oriQueueSize = (Long) conn.getAttribute(producerActiveMQ, "QueueSize");
                // newQueueSize = (Long) conn.getAttribute(consumeActiveMQ, "QueueSize");
            }
            // while(oriQueueSize.intValue() != 0 || newQueueSize.intValue() != 0);

            // Stop the connection â€” good practice but redundant here
            connection.stop();
            //System.exit(0);
        } catch (JMSException e) {
            e.printStackTrace();
        }

    }
}

