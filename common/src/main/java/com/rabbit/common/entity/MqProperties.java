package com.rabbit.common.entity;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 消息队列的基础配置信息
 *
 * @author zxf
 */
@Data
public class MqProperties {

    private final Distributed distributed = new Distributed();

    private final Rabbit rabbit = new Rabbit();

    @Data
    public static class Distributed {

        /**
         * 提交ack 失败最大重试次数
         */
        private Integer commitMaxRetries = 5;

        /**
         * 接收消息 ack 失败最大尝试次数
         */
        private Integer receiveMaxRetries = 5;


        /**
         * Prepare和Ready状态消息超时时间 默认为3分钟
         * 单位为秒
         */
        private long timeOut = 3 * 60;

        /**
         * returnCallback的状态过期时间 默认为1天
         * 单位为秒
         */
        private long returnCallbackTTL = 24 * 60 * 60;
    }

    @Data
    public static class Rabbit {

        /**
         * {@link org.springframework.amqp.core.AcknowledgeMode}
         * <p>
         * 0 AUTO
         * 1 MANUAL
         * 2 NONE
         */
        private int acknowledgeMode = 1;

        /**
         * 每个消费者可能未完成的未确认消息的数量。
         */
        private Integer prefetchCount = null;

        /**
         * 为每个已配置队列创建的消费者数
         */
        private Integer consumersPerQueue = null;

        /**
         * 是否持久化，指是否保存到erlang自带得数据库mnesia中，即重启服务是否消失
         */
        private boolean durable = true;

        /**
         * 是否排外，指当前定义的队列是connection中的channel共享的，其他connection连接访问不到
         */
        private boolean exclusive = false;

        /**
         * 是否自动删除，指当connection.close时队列删除
         */
        private boolean autoDelete = false;

        /**
         * 是否初始化消息监听者， 若服务只是Producer则关闭
         */
        private boolean listenerEnable = false;

        /**
         * 通道缓存
         */
        private Integer channelCacheSize = null;
    }
}

