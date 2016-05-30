package com.message;

/**
 * Created by xy on 5/28/2016.
 */
import javax.jms.*;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;



public class Producer {

    public static void main (String[] args)
            throws Exception {


        // Create a connection factory referring to the broker host and port

        ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory
                ("tcp://localhost:61616");

        // Note that a new thread is created by createConnection, and it
        //  does not stop even if connection.stop() is called. We must
        //  shut down the JVM using System.exit() to end the program
        Connection connection = factory.createConnection();

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
        //TextMessage consumeMessage;
        TextMessage sendMessage;

        // Create a simple text message and send it
        for(int i = 0; i < 10; i++){
            sendMessage = session.createTextMessage ("Hello, world! Number " + i);
            producer.send(sendMessage);
        }

     /*   JMXServiceURL url = new JMXServiceURL("service:jmx:rmi:///jndi/rmi://localhost:1099/jmxrmi");
        JMXConnector jmxc = JMXConnectorFactory.connect(url);
        MBeanServerConnection conn = jmxc.getMBeanServerConnection();*/

        String mes;
        int i = 0;
        //Long oriQueueSize = (Long) conn.getAttribute(producerActiveMQ, "QueueSize");
        //Long newQueueSize = (Long) conn.getAttribute(consumeActiveMQ, "QueueSize");
        //while(conn.getAttribute(producerActiveMQ, "QueueSize") == "0" || conn.getAttribute(consumeActiveMQ, "QueueSize") != "0")
        while(i<10){
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

        System.exit(0);
    }

}
