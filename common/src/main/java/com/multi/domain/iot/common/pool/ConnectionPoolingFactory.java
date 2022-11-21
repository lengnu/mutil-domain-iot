package com.multi.domain.iot.common.pool;

import io.netty.channel.Channel;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @BelongsProject: Multi-Domain-IoT
 * @BelongsPackage: com.multi.domain.iot.common.pool
 * @Author: duwei
 * @Date: 2022/11/21 14:21
 * @Description: 连接池工厂
 */
public abstract class ConnectionPoolingFactory implements ConnectionPooling{
    protected Map<InetSocketAddress,Channel> channelMap = new ConcurrentHashMap<>();

    @Override
    public Channel getChannel(InetSocketAddress inetSocketAddress) throws InterruptedException {
        if (channelMap.get(inetSocketAddress) == null){
            createChannel(inetSocketAddress);
        }
        return channelMap.get(inetSocketAddress);
    }

    private  void createChannel(InetSocketAddress inetSocketAddress) throws InterruptedException {
        Channel channel = doCreateChannel(inetSocketAddress);
        channelMap.put(inetSocketAddress,channel);
    }

    protected abstract Channel doCreateChannel(InetSocketAddress inetSocketAddress) throws InterruptedException;
}
