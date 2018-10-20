package com.edu.mq.rabbitmq.send_consume;

import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public class Send {
    public static void main(String[] args) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();

        /**第一种**/
//        factory.setHost("192.168.74.128");
//        factory.setPort(5672);
//        factory.setUsername("wangwei");
//        factory.setPassword("wangwei");
//        factory.setVirtualHost("/");

        /**第二种*/
//        factory.setUsername("wangwei");
//        factory.setPassword("wangwei");
//        factory.setUri("amqp://192.168.74.128:5672");
//        factory.setUri("amqps://192.168.74.128:5672");//SSL

        /**第三种*/
//        factory.setUri("amqp://wangwei:wangwei@192.168.71.128:5672");
        //vLog-指定的VirtualHost
        factory.setUri("amqp://wangwei:wangwei@192.168.71.128:5672/vLog");
//        factory.setUri("amqps://wangwei:wangwei@192.168.74.128:5672");

        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        BasicProperties props = new BasicProperties().builder().deliveryMode(2).contentEncoding("UTF-8").build();
        //""-默认exchange
//        channel.basicPublish("","debug",props,"忘记密码，验证码654321。".getBytes());
//        channel.basicPublish("login","login",props,"登录校验".getBytes());
//        channel.basicPublish("log","action.log",props,"项羽虞姬".getBytes());
//        channel.basicPublish("wei.debug","wei.debug",props,"海棠未眠".getBytes());
        channel.basicPublish("log","log",props,"岁月静好".getBytes());

        System.out.println("============>over<===========");
        channel.close();
        connection.close();
    }
}
