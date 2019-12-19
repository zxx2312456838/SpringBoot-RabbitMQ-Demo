package com.rabbit.provider.untils;

import com.rabbit.common.constant.RabbitMQConstant;
import com.rabbit.common.entity.EventMessage;
import com.rabbit.common.entity.MqProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import java.util.UUID;


/**
 * 消息队列发送工具类
 */
@Configuration
public class RabbitMQSend implements RabbitTemplate.ConfirmCallback, RabbitTemplate.ReturnCallback {

    private static final Logger log = LoggerFactory.getLogger(RabbitMQSend.class);

    private RabbitTemplate rabbitTemplate;
    private MqProperties config;


    @Autowired
    public RabbitMQSend(RabbitTemplate rabbitTemplate) {
        super();
        this.rabbitTemplate = rabbitTemplate;
        /**
         * 默认false
         * 当mandatory标志位设置为true时
         * 如果exchange根据自身类型和消息routingKey无法找到一个合适的queue存储消息
         * 那么broker会调用basic.return方法将消息返还给生产者(下边的returnCallback)
         * 当mandatory设置为false时，出现上述情况broker会直接将消息丢弃
         */
        this.rabbitTemplate.setMandatory(true);
        this.rabbitTemplate.setReturnCallback(this);
        this.rabbitTemplate.setConfirmCallback(this);
    }

    /**
     * 发布/订阅模式发送
     */
    public void routeSend(EventMessage message) {
        //在fanoutExchange中在绑定Q到X上时，会自动把Q的名字当作bindingKey。
        this.rabbitTemplate.convertAndSend(RabbitMQConstant.FANOUT_EXCHANGE, "", message);
    }

    /**
     * 简单模式发送
     */
    public void simplSend(EventMessage message) {
        this.rabbitTemplate.convertAndSend(RabbitMQConstant.QUEUE_USER, message, new CorrelationData(
                "m" + UUID.randomUUID()));
    }

    /**
     * 路由模式发送
     */
    public void routingSend(String routingKey, EventMessage message) {
        this.rabbitTemplate.convertAndSend(RabbitMQConstant.DIRECT_EXCHANGE, routingKey, message);
    }

    /**
     * 主题模式发送
     */
    public void topicSend(String routingKey, EventMessage message) {
        this.rabbitTemplate.convertAndSend(RabbitMQConstant.TOPIC_EXCHANGE, routingKey, message, new CorrelationData(
                "m" + UUID.randomUUID()));
    }

    /**
     * 死信模式发送,用于定时任务处理
     */
    public void beadSend(String routingKey, EventMessage message) {
        this.rabbitTemplate.convertAndSend(RabbitMQConstant.DEAD_EXCHANGE, routingKey, message);
    }

    /**
     * 设置消息参数
     */
    private Message setMessage(String json) {
        MessageProperties messageProperties = new MessageProperties();
        Message message = new Message(json.getBytes(), messageProperties);
        // 消息持久化
        message.getMessageProperties().setDeliveryMode(MessageDeliveryMode.PERSISTENT);
        // 消息过期时间
        messageProperties.setExpiration(10 + "000");

        return message;
    }

    /**
     * ack为true,只是单纯的保证消息发送给了交换机，有没有路由到对应的队列不知道
     *
     * @param correlationData
     * @param ack
     * @param cause
     */
    @Override
    public void confirm(CorrelationData correlationData, boolean ack, String cause) {
        if (ack) {
            log.info("发送到交换机成功：消息ID：[{}]", correlationData.getId());
        } else {
            log.error("发送到交换机失败：消息ID：[{}],原因：[{}]", correlationData.getId(), cause);
        }
    }

    /**
     * 消息发送失败回调，成功不回调
     *
     * @param message
     * @param replyCode
     * @param replyText
     * @param exchange
     * @param routingKey
     */
    @Override
    public void returnedMessage(Message message, int replyCode, String replyText, String exchange, String routingKey) {
        String correlationDataID = message.getMessageProperties().getHeader("spring_returned_message_correlation");

        log.error("消息路由到queue异常：消息ID:[{}],replyCode:[{}],replyText:[{}],exchange:[{}],routingKey:[{}]",
                correlationDataID, replyCode, replyText, exchange, routingKey);
    }
}