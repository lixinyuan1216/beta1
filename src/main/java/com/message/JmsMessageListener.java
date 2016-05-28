package com.message;
import org.springframework.stereotype.Service;

/**
 * Created by xy on 5/28/2016.
 */
@Service
/**
 * POJO class, have handleMessage(...) method.
 * The return of handleMessage(...) will be
 * automatically send back to message.getJMSReplyTo().
 */
public class JmsMessageListener {

    public String handleMessage(String text) {
        System.out.println("Received: " + text);
        return "ACK from handleMessage";
    }
}