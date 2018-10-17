package com.edu.mq.rabbitmq.send_consume;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.util.concurrent.TimeUnit;

public class Consume {
    public static void main(String[] args) throws Exception{
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("192.168.74.128");
        factory.setPort(5672);
        factory.setUsername("wangwei");
        factory.setPassword("wangwei");
        factory.setVirtualHost("/");



        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.basicConsume("sys.action.queue",true,new SimpleConsumer(channel));

        TimeUnit.SECONDS.sleep(3600);
        System.out.println("============>over<===========");
        channel.close();
        connection.close();
    }
}
