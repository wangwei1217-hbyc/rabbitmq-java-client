package com.edu.mq.rabbitmq;

import com.rabbitmq.client.*;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * rabbitmq中各个组件：exchange、queue、binding声明及相关操作
 */
public class App {
    public static void main(String[] args) throws Exception{
        ConnectionFactory factory = new ConnectionFactory();
//        factory.setHost("192.168.109.128");
        factory.setHost("192.168.109.128");
        factory.setPort(5672);
        factory.setUsername("wangwei");
        factory.setPassword("wangwei");
        factory.setVirtualHost("/");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        /**	创建Exchange（可重复执行，不会重复创建）**/
        //创建一个名为wei.info的类型为“direct”的exchange。非持久化
//        channel.exchangeDeclare("wei.info", BuiltinExchangeType.DIRECT);
        //第三个参数：durable（是否持久化）。true-是；false-否

        channel.exchangeDeclare("wei.trace",BuiltinExchangeType.DIRECT,true);
        channel.exchangeDeclare("wei.error",BuiltinExchangeType.DIRECT,true);
        AMQP.Exchange.DeclareOk exOk = channel.exchangeDeclare("wei.debug", BuiltinExchangeType.DIRECT, true);
        System.out.println(exOk);
        /*
        第四个参数autoDelete:  true-当最后一个绑定被删除后，该exchange自动被删除
        第五个参数internal:是否是内部专用exchange，true-就意味着我们不能往该exchange里面发送消息
         */
//        AMQP.Exchange.DeclareOk weiErrorOk = channel.exchangeDeclare("wei.error", BuiltinExchangeType.TOPIC,
//                true, false, false, null);
//        System.out.println(weiErrorOk);

        //第五个参数arguments:map格式的参数
//        Map<String,Object> arguments = new HashMap<>();
//        arguments.put("size","36D");
//        channel.exchangeDeclare("log.warn",BuiltinExchangeType.TOPIC,true,false,false,arguments);

        //创建exchange（异步操作,没有返回值）
//        channel.exchangeDeclareNoWait("wei.trace", BuiltinExchangeType.TOPIC,
//                true, false, false, null);

        /**
         * 判断Exchange是否存在.如果在当前VirtualHost下不存在该exchange，会抛异常。
         * **/
//        AMQP.Exchange.DeclareOk passive = channel.exchangeDeclarePassive("wei.error");
//        System.out.println(passive);

        //不存在会报错
//        AMQP.Exchange.DeclareOk passive2 = channel.exchangeDeclarePassive("wei.error2");
//        System.out.println(passive2);

        /**
         * 删除Exchange.可重复执行
         */
//        channel.exchangeDelete("wei.debug");
//        channel.exchangeDelete("wei.error");
//        channel.exchangeDelete("log.warn");

        /**queue的创建(可重复执行，不会重复创建)***/

        /*
        第三个参数exclusive: true-排他性（只在当前Connection生命周期内有效，一旦该Connection关闭，该queue会自动删除）
         */
        AMQP.Queue.DeclareOk info_quque_ok = channel.queueDeclare("wei.debug_queue", true, false, false, null);
        System.out.println(info_quque_ok);

        //创建一个排他queue
//        AMQP.Queue.DeclareOk exclusive_queue_ok = channel.queueDeclare("wei.exclusive_queue", true, true, false, null);
//        System.out.println(exclusive_queue_ok);

        //创建queue（异步操作,没有返回值）
//        channel.queueDeclareNoWait("wei.trace_queue",true,false,false,null);

        /**判断queue是否存在。不存在会抛出异常*/
//        AMQP.Queue.DeclareOk queuePassive = channel.queueDeclarePassive("wei.info_queue");
//        System.out.println(queuePassive);

//        AMQP.Queue.DeclareOk queuePassive2 = channel.queueDeclarePassive("wei.info_queue_2");
//        System.out.println(queuePassive2);


        /**
         * 删除queue
         */
//        channel.queueDelete("wei.info_queue");

        /**exchange和queue绑定(可重复执行，不会重复绑定)**/
        AMQP.Queue.BindOk bindOk = channel.queueBind("wei.debug_queue", "wei.debug", "wei.debug", null);
        System.out.println(bindOk);

//        bindOk = channel.queueBind("wei.info_queue", "wei.error", "#.wei.error.#", null);
//        System.out.println(bindOk);

        //创建exchange和queue的绑定（异步，没有返回值）
//        channel.queueBindNoWait("wei.trace_queue", "wei.trace", "#.wei.trace.*", null);

        /**exchange 和 exchange绑定(可重复执行，不会重复绑定)**/
        AMQP.Exchange.BindOk bindExchangeOk = channel.exchangeBind("wei.trace", "wei.error", "error.trace");
        System.out.println(bindExchangeOk);

        /**exchange和queue、exchange解绑(可重复执行)*/
//        channel.queueUnbind("wei.info_queue", "wei.error", "#.wei.error.#", null);
        AMQP.Queue.UnbindOk unbindOk = channel.queueUnbind("wei.debug_queue", "wei.debug", "wei.debug");
        System.out.println(unbindOk);

        channel.exchangeUnbind("wei.trace", "wei.error", "error.trace");

//        TimeUnit.SECONDS.sleep(10);
        channel.close();
        connection.close();
        System.out.println("=========over=======");
    }
}
