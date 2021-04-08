package com.edu.mq.rabbitmq.send_consume;

import com.rabbitmq.client.*;
import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.impl.DefaultExceptionHandler;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class Send {
    public static void main(String[] args) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();

        /**第一种**/
//        factory.setHost("192.168.109.128");
//        factory.setPort(5672);
//        factory.setUsername("wangwei");
//        factory.setPassword("wangwei");
//        factory.setVirtualHost("/");

        /**第二种*/
//        factory.setUsername("wangwei");
//        factory.setPassword("wangwei");
//        factory.setUri("amqp://192.168.109.128:5672");
//        factory.setUri("amqps://192.168.109.128:5672");//SSL

        /**第三种*/
        factory.setUri("amqp://wangwei:wangwei@192.168.109.128:5672");
        //vLog-指定的VirtualHost
//        factory.setUri("amqp://wangwei:wangwei@192.168.109.128:5672/vLog");
//        factory.setUri("amqps://wangwei:wangwei@192.168.109.128:5672");

        /***
         * 自定义PublishConfirm发生异常时的处理逻辑
         */
        factory.setExceptionHandler(new DefaultExceptionHandler(){
            @Override
            public void handleConfirmListenerException(Channel channel, Throwable exception) {
                System.out.println("********** ConfirmListener发生异常 ************");
                exception.printStackTrace();
//                super.handleConfirmListenerException(channel, exception);
            }
        });

        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        /**
         * 使当前Channel处于确认模式（处于事务模式的Channel，不能设置为确认模式）
         */
        channel.confirmSelect();
        //使当前channel处于事务模式(事务模式就相当于发送消息和接收消息是同步的，违背了消息中间件消息解耦的初衷，影响性能。一般不使用)
//        channel.txSelect();

        /**
         * 添加消息发送确认Listener
         */
        channel.addConfirmListener(new ConfirmListener() {
            /**
             * deliveryTag:投递的标识
             * 当Channel设置成confirm模式时，发布的每一条消息都会获得一个唯一的deliveryTag
             * 任何Channel上发布的第一条消息的deliveryTag为1，此后的每一条消息都会加1
             * deliveryTag在Channel范围内是唯一的
             */

            /**
             * deliveryTag 消息ID
             * multiple 是否批量
             *   -如果是true，就意味着，小于等于deliveryTag的消息都处理成功了
             *   -如果是false，就意味着，只是成功了deliveryTag这一条消息
             */
            @Override
            public void handleAck(long deliveryTag, boolean multiple) throws IOException {
                //消息处理成功触发该方法
                System.out.println("+++++++++++++消息处理(PublishConfirm)成功++++++++++");
                System.out.println("deliveryTag: " + deliveryTag);
                System.out.println("   multiple: " + multiple);
                //TODO 处理成功发送的消息
                throw new IOException("数据库处理异常，确认失败...");
            }

            /**
             * deliveryTag 消息ID
             * multiple 是否批量
             *   如果是true，就意味着，小于等于deliveryTag的消息都处理失败了
             *   如果是false，就意味着，只是失败了deliveryTag这一条消息
             */
            @Override
            public void handleNack(long deliveryTag, boolean multiple) throws IOException {
                //消息处理失败触发该方法
                System.out.println("+++++++++++++消息处理(PublishConfirm)失败了++++++++++");
                System.out.println("deliveryTag: " + deliveryTag);
                System.out.println("   multiple: " + multiple);
                //TODO 处理发送失败的消息
            }
        });

        /**
         * deliveryMode(2): 1-消息不持久化；2-消息持久化(对应rabbitMQ管理页面Publish message时的Delivery mode选项)
         */
        BasicProperties props = new BasicProperties().builder().deliveryMode(2).contentEncoding("UTF-8").build();
        //""-默认exchange
//        channel.basicPublish("","debug",props,"忘记密码，验证码654321。".getBytes());
        /**
         * 第三个参数mandatory：
         * 如果mandatory有设置，则当消息不能路由到队列中去的时候，会触发return method,
           如果mandatory没有设置，则当消息不能路由到队列中去的时候，server会删除该消息

         */
        channel.addReturnListener(new ReturnListener() {
            @Override
            public void handleReturn(int replyCode, String replyText, String exchange, String routingKey, BasicProperties properties, byte[] body) throws IOException {
                System.out.println("--------------handleReturn-----------------");
                System.out.println("replyCode: "+replyCode);
                System.out.println("replyText: "+replyText);
                System.out.println("exchange: "+exchange);
                System.out.println("routingKey: "+routingKey);
                System.out.println(properties);
                System.out.println("消息："+new String(body));
            }
        });

        /**
         * 当Channel设置成confirm模式时，发布的每一条消息都会获得一个唯一的deliveryTag
         * deliveryTag在basicPublish执行的时候加一
         */
        channel.basicPublish("login","login.reg",true,props,"登录校验。验证码：12138".getBytes());
        /**
         * 等待确认，如此设置，就相当于消息是一条一条确认的。
         * 必须要在处于确认模式的Channel上调用waitForConfirms、waitForConfirmsOrDie方法
         */
//        channel.waitForConfirms();
//        channel.basicPublish("login","login.reg",true,props,"登录校验。验证码：12139".getBytes());
//        channel.waitForConfirms();
//        channel.basicPublish("login","login.reg",true,props,"登录校验。验证码：12140".getBytes());
//        channel.waitForConfirms(2000l);//毫秒

        /**
         * 内部调用channel.waitForConfirms(0l)方法，当该调用返回false 或者 该调用抛出TimeoutException异常，
         * 就close掉当前Channel
         */
//        channel.waitForConfirmsOrDie();
//        channel.basicPublish("log","action.log",props,"项羽虞姬".getBytes());
//        channel.basicPublish("wei.debug","wei.debug",props,"海棠未眠".getBytes());
//        channel.basicPublish("log","log",props,"岁月静好".getBytes());

        System.out.println("============>over<===========");
        TimeUnit.SECONDS.sleep(20);
        channel.close();
        connection.close();
    }
}
