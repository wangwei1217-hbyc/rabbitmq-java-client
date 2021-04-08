package com.edu.mq.rabbitmq.send_consume;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

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

        /**
         * 消费端消息消费确认(Consumer Acknowledgements) (broker->consumer)
              -channel.basicConsume():该方法进行消息消费时，设置autoAck为false(不自动确认)
              -在具体的消息消费回调方法中进行手动消息确认或拒绝(即ack或Nack)
         */

        if(properties.getHeaders().get("error") != null){
            //进行消息拒绝
            try {
                TimeUnit.SECONDS.sleep(4);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            /**
             * 支持批量拒绝消息
             *   requeue:拒绝后是否重新入队列。true-重新入队列.该值要慎重设置，设置为true有可能导致死循环
             */
//            this.getChannel().basicNack(envelope.getDeliveryTag(),false,true);
            /**只支持拒绝单条消息*/
            this.getChannel().basicReject(envelope.getDeliveryTag(),false);

            //无法确认的消息，建议先保存，然后进行确认
            System.out.println("-----消息无法消费，拒绝消息！-----");
            return;
        }
        /**
         * multiple:是否批量确认
         */
        this.getChannel().basicAck(envelope.getDeliveryTag(),false);
        System.out.println("...消息消费成功拉....");

        /***
         * 异常模拟
         */
        throw new NullPointerException("id不能为空");
    }
}
