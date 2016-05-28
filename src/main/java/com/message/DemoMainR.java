package com.message;

/**
 * Created by xy on 5/28/2016.
 */
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class DemoMainR {

    public static void main(String[] args) {
        // create Spring context
        ApplicationContext ctx = new ClassPathXmlApplicationContext("receiver.xml");

        // sleep for 1 second
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // close application context
        ((ClassPathXmlApplicationContext)ctx).close();
    }
}