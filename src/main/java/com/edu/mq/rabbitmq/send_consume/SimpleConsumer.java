package com.edu.mq.rabbitmq.send_consume;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

import java.io.IOException;

public class SimpleConsumer extends DefaultConsumer {

    public SimpleConsumer(Channel channel) {
        super(channel);
    }

    /**收到消息进行回调*/
    @Override
    public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
        System.out.println("=========收到消息啦===========");
        /*
        consumerTag=amq.ctag-_nHVLtTAHNmvB2k83DhWkQ
        envelope=Envelope(deliveryTag=1, redeliver=false, exchange=log, routingKey=action.log)
        properties=#contentHeader<basic>(content-type=null, content-encoding=UTF-8, headers=null,
             delivery-mode=2, priority=null, correlation-id=null, reply-to=null, expiration=null,
             message-id=null, timestamp=null, type=null, user-id=null, app-id=null, cluster-id=null)
        body=日志记录2333
         */
        System.out.println("consumerTag="+consumerTag);
        System.out.println("envelope="+envelope);
        System.out.println("properties="+properties);
        System.out.println("body="+new String(body));
    }
}
