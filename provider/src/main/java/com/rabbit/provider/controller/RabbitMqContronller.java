package com.rabbit.provider.controller;

import com.rabbit.common.entity.EventMessage;
import com.rabbit.provider.untils.RabbitMQSend;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 生产者
 */
@RestController
public class RabbitMqContronller {

    @Autowired
    private RabbitMQSend send;


    @ApiOperation(value = "简单模式发送json消息", notes = "简单模式发送json消息")
    @ApiImplicitParam(name = "message", value = "消息类", required = true, dataType = "EventMessage")
    @GetMapping("/simplSend")
    public String simplSend(@RequestBody EventMessage message) {
        try {
            this.send.simplSend(message);
            return "succeed";
        } catch (Exception e) {
            e.printStackTrace();
            return "error";
        }
    }

    @ApiOperation(value = "订阅模式发送json消息", notes = "订阅模式发送json消息")
    @ApiImplicitParam(name = "message", value = "消息类", required = true, dataType = "EventMessage")
    @GetMapping("/routeSend")
    public String routeSend(@RequestBody EventMessage message) {
        try {
            this.send.routeSend(message);
            return "succeed";
        } catch (Exception e) {
            e.printStackTrace();
            return "error";
        }
    }

    @ApiOperation(value = "路由模式发送json消息", notes = "路由模式发送json消息")
    @ApiImplicitParam(name = "message", value = "消息类", required = true, dataType = "EventMessage")
    @GetMapping("/routingSend")
    public String routingSend(@RequestBody EventMessage message, String routingKey) {
        try {
            this.send.routingSend(routingKey, message);
            return "succeed";
        } catch (Exception e) {
            e.printStackTrace();
            return "error";
        }
    }

    @ApiOperation(value = "主题模式发送json消息", notes = "主题模式发送json消息")
    @ApiImplicitParam(name = "message", value = "消息类", required = true, dataType = "EventMessage")
    @PostMapping("/topicSend")
    public String topicSend(@RequestBody EventMessage message, String routingKey) {
        try {
            this.send.topicSend(routingKey, message);
            return "succeed";
        } catch (Exception e) {
            e.printStackTrace();
            return "error";
        }
    }
}