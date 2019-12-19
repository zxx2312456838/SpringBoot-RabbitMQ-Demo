package com.rabbit.common.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 消息类
 *
 * @author zxf
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventMessage implements Serializable {

    private static final long serialVersionUID = -9203358002484642594L;

    private String name;
    private String desc;
}
