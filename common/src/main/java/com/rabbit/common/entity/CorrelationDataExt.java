package com.rabbit.common.entity;

import lombok.Data;
import org.springframework.amqp.rabbit.connection.CorrelationData;

/**
 * 扩展 CorrelationData
 *
 * @author zxf
 */
@Data
public class CorrelationDataExt extends CorrelationData {

    private String coordinator;

    private Integer maxRetries;

    private EventMessage message;

    public CorrelationDataExt(String id, String coordinator, Integer maxRetries, EventMessage message) {
        super(id);
        this.coordinator = coordinator;
        this.maxRetries = maxRetries;
        this.message = message;
    }
}
