package com.edu.mq.rabbitmq.send_consume;

import com.rabbitmq.client.*;
import com.rabbitmq.client.impl.DefaultExceptionHandler;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * --消息消费
 * --Connection&Channel信息配置
 */
public class Consume {
    public static void main(String[] args) throws Exception{
        ConnectionFactory factory = new ConnectionFactory();
//        factory.setHost("192.168.74.128");
//        factory.setPort(5672);
//        factory.setUsername("wangwei");
//        factory.setPassword("wangwei");
//        factory.setVirtualHost("/");

        factory.setUri("amqp://wangwei:wangwei@192.168.109.128:5672");
        //vLog-指定的VirtualHost
//        factory.setUri("amqp://wangwei:wangwei@192.168.109.128:5672/vLog");

        /***
         * 自定义异常处理
         */
        factory.setExceptionHandler(new DefaultExceptionHandler(){
            @Override
            public void handleConsumerException(Channel channel, Throwable exception, Consumer consumer, String consumerTag, String methodName) {
                System.out.println("-------------消费消息发生异常------------");
                System.out.println("channel: "+channel);
                System.out.println("consumer: "+consumer);
                System.out.println("consumerTag: "+consumerTag);
                System.out.println("methodName: "+methodName);
                exception.printStackTrace();
//                super.handleConsumerException(channel, exception, consumer, consumerTag, methodName);
            }
        });

        /*
        指定Connection的Client properties.在原来的参数集合基础上追加自定义属性
        也可以：Map<String,Object> clientProperties = new HashMap<String,Object>();
        如此做，则会覆盖原有的rabbitmq已默认设置的一些属性.
         */

        Map<String,Object> clientProperties = factory.getClientProperties();
        clientProperties.put("author","张三");
        clientProperties.put("version","v1.0");
        clientProperties.put("online_date","2019-10-10");
        factory.setClientProperties(clientProperties);


//        Connection connection = factory.newConnection();

        //指定ConnectionName
        Connection connection = factory.newConnection("debug日志处理");
        /**
        默认channel标号是自增的。从1开始
        192.168.71.128:5672 (1)
        192.168.71.128:5672 (2)
         */
        Channel channel = connection.createChannel();

//        Channel channel2 = connection.createChannel();

        /*
        也可以指定Channel的标号
        192.168.71.128:5672 (10)
        192.168.71.128:5672 (20)
         */
//        Channel channel10 = connection.createChannel(10);
//        Channel channel20 = connection.createChannel(20);

//        channel.basicConsume("sys.action.queue",true,new SimpleConsumer(channel));
//        String consumerTag = channel.basicConsume("login", true, new SimpleConsumer(channel));

        /**
         *第二个参数autoAck:是否自动确认。默认true
         *第三个参数：指定consumerTag
         */
//        String consumerTag = channel.basicConsume("login", true,"login_consumer", new SimpleConsumer(channel));
        String consumerTag = channel.basicConsume("login", false,"login_consumer", new SimpleConsumer(channel));

        System.out.println("consumerTag="+consumerTag);

        TimeUnit.SECONDS.sleep(20);
        System.out.println("============>over<===========");
        channel.close();
        connection.close();
    }
}
