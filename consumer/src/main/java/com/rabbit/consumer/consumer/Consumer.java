package com.rabbit.consumer.consumer;

import com.rabbit.common.constant.RabbitMQConstant;
import com.rabbit.common.entity.EventMessage;
import com.rabbit.consumer.untils.RabbitMQSend;
import com.rabbitmq.client.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;
import java.util.Random;

/**
 * 消费监听
 */

@Component
public class Consumer {

    private static final Logger log = LoggerFactory.getLogger(Consumer.class);


    @Autowired
    private RabbitMQSend send;


    /**
     * 消费监听--业务异常触发重试机制，达到最大次数直接抛异常，异常后消息直接丢弃
     */
    @RabbitListener(queues = RabbitMQConstant.QUEUE_USER, errorHandler = "rabbitListenerErrorHandler")
    public void simplConsumer1(Message message, EventMessage eventMessage, Channel channel, @Headers Map<String, Object> map) {
        try {
            log.info("queue_user 收到消息 :[{}] ", eventMessage.toString());

            // 模拟随机处理异常
            int i = 1 / new Random().nextInt(2);

            //手动ACK
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (IOException e) {
            log.error("queue_user 消费消息异常，cause:[{}]", e.getMessage());
        }
    }

    /**
     * 消费监听--业务异常进行一系列自定义操作
     * 放死信队列，入库等。
     */
    @RabbitListener(queues = RabbitMQConstant.QUEUE_ORDER)
    public void simplConsumer2(Message message, EventMessage eventMessage, Channel channel, @Headers Map<String, Object> map) {
        String json = "";
        try {
            log.info("queue_order 收到消息 :[{}] ", eventMessage.toString());

            // 模拟随机处理异常
            int i = 1 / new Random().nextInt(2);

            //手动ACK
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (Exception e) {
            // 也可以放到数据库，然后用定时任务刷，或者人工干预
            log.error("queue_order 消费消息异常，将消息转发到死信队列，cause：[{}]", e.getMessage());
            try {
                // 确认异常消息
                channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, false);
            } catch (IOException ex) {
                log.error("确认消息异常");
            }
            MessageProperties messageProperties = new MessageProperties();
            //设置消息过期时间,单位秒
            messageProperties.setExpiration(10 + "000");
            Message message2 = new Message(json.getBytes(), messageProperties);
            message2.getMessageProperties().setDeliveryMode(MessageDeliveryMode.PERSISTENT);
            //这里的key应该传死信队列绑定死信交换机的路由key,这里我们传key1
            this.send.beadSend(RabbitMQConstant.ROUTING_KEY1, message2);
        }
    }

    /**
     * 当死信队列的消息过期后,会通过死信交换机把过期消息发送到这里
     */
    @RabbitListener(queues = RabbitMQConstant.CONSUMER_BEAD_QUEUE)
    public void simplConsumer3(Message message, Channel channel, @Headers Map<String, Object> map) {
        try {
            log.info("consumer_bead_queue 收到消息 :[{}] ", message.getBody().toString());
            //手动ACK
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (IOException e) {
            log.error("死信队列 消费消息异常，cause：[{}]", e.getMessage());
            try {
                // 确认异常消息
                channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, false);
            } catch (IOException ex) {
                log.error("确认消息异常");
            }
        }
    }
}