package com.edu.mq.rabbitmq.send_consume;

import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConfirmListener;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.TreeSet;
import java.util.concurrent.TimeUnit;

/**
 * 消息发送确认(模拟：消息发送后先进行入库操作，待消息确认后，再从库中删除)
 * Created by wangwei on 2019/10/25 0025.
 */
public class ConfirmSend {
    static Long id = 0L;
    static TreeSet<Long> idTags = new TreeSet<>();
    private static Long send(Channel channel,byte[] body)throws Exception{
        BasicProperties basicProperties = new BasicProperties.Builder().deliveryMode(2).contentEncoding("UTF-8").build();
        channel.basicPublish("login","login.confirm",basicProperties,body);
        return ++id;
    }
    public static void main(String[] args) throws Exception{
        ConnectionFactory factory = new ConnectionFactory();
        factory.setUri("amqp://wangwei:wangwei@192.168.109.128:5672");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();
        channel.confirmSelect();
        /**
         * deliveryTag:投递的标识
         * 当Channel设置成confirm模式时，发布的每一条消息都会获得一个唯一的deliveryTag
         * 任何Channel上发布的第一条消息的deliveryTag为 1，此后的每一条消息都会加 1
         * deliveryTag在Channel范围内是唯一的
         */
        channel.addConfirmListener(new ConfirmListener() {
            @Override
            public void handleAck(long deliveryTag, boolean multiple) throws IOException {
                //消息处理成功触发该方法
                System.out.println("+++++++++++++消息处理成功++++++++++");
                System.out.println("deliveryTag: " + deliveryTag);
                System.out.println("   multiple: " + multiple);
                //TODO 处理成功发送的消息
                if(multiple){
                    //批量确认
//                    for(Long _id : new TreeSet<>(idTags.headSet(deliveryTag+1))) {
//                        idTags.remove(_id);
//                    }
                    for(long i = deliveryTag;i >= 1;i--){
                        if(idTags.contains(i)){
                            idTags.remove(i);
                        }
                    }
                }else {
                    //单个确认
                    idTags.remove(deliveryTag);
                }
                System.out.println("未确认的消息："+idTags);
            }

            @Override
            public void handleNack(long deliveryTag, boolean multiple) throws IOException {
                //消息处理失败触发该方法
                System.out.println("+++++++++++++失败了++++++++++");
                System.out.println("deliveryTag: " + deliveryTag);
                System.out.println("   multiple: " + multiple);
                //TODO 处理发送失败的消息
            }
        });

        Long id = send(channel, "HelloJava".getBytes());
        //ID是需要入库或者保存的
        idTags.add(id);
        id = send(channel, "HelloPHP".getBytes());
        //ID是需要入库或者保存的
        idTags.add(id);
        id = send(channel, "HelloC++".getBytes());
        //ID是需要入库或者保存的
        idTags.add(id);

//        channel.waitForConfirmsOrDie();
        TimeUnit.SECONDS.sleep(5);
//        channel.close();
        connection.close();
        System.out.println("------------OVER--------------");
    }
}
