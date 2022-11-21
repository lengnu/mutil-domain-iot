package com.multi.domain.iot.common.pool;

import io.netty.channel.Channel;

import java.net.InetSocketAddress;

/**
 * @BelongsProject: Multi-Domain-IoT
 * @BelongsPackage: com.multi.domain.iot.common.pool
 * @Author: duwei
 * @Date: 2022/11/21 14:17
 * @Description: 连接池接口
 */
public interface ConnectionPooling {
    /**
     * 根据地址获取连接
     */
    Channel getChannel(InetSocketAddress inetSocketAddress) throws InterruptedException;
}
