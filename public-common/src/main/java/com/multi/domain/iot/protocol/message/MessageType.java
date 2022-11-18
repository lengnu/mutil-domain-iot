package com.multi.domain.iot.protocol.message;

/**
 * @BelongsProject: Multi-Domain-IoT
 * @BelongsPackage: com.multi.domain.iot.protocol.message
 * @Author: duwei
 * @Date: 2022/11/17 11:27
 * @Description: 消息类型
 */
public interface MessageType {
    /**
     * 获取公共参数请求数据包
     */
    byte FETCH_PUBLIC_PARAMETER_REQUEST_PACKET = 1;
    /**
     * 获取公共参数响应数据包
     */
    byte FETCH_PUBLIC_PARAMETER_RESPONSE_PACKET = 2;
    /**
     * 注册公钥请求
     */
    byte ENROLL_INFORMATION_REQUEST = 3;
    /**
     * 注册公钥响应
     */
    byte ENROLL_INFORMATION_RESPONSE = 4;
}
