package com.multi.domain.iot.handler;

import com.multi.domain.iot.parameter.TransmitPublicParameter;
import com.multi.domain.iot.parameter.TransmitPublicParameterBuilder;
import com.multi.domain.iot.protocol.request.FetchPublicParameterRequestPacket;
import com.multi.domain.iot.protocol.response.FetchPublicParameterResponsePacket;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

import java.io.FileInputStream;
import java.io.InputStream;

/**
 * @BelongsProject: Multi-Domain-IoT
 * @BelongsPackage: com.multi.domain.iot.handler
 * @Author: duwei
 * @Date: 2022/11/17 16:45
 * @Description: 客户请求公共参数处理器
 */
@Slf4j
@ChannelHandler.Sharable
public class FetchPublicParameterRequestHandler extends SimpleChannelInboundHandler<FetchPublicParameterRequestPacket> {
    private FetchPublicParameterRequestHandler(){

    }

    public static final FetchPublicParameterRequestHandler INSTANCE = new FetchPublicParameterRequestHandler();

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FetchPublicParameterRequestPacket requestPacket) throws Exception {
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream("public.properties");
            TransmitPublicParameter transmitPublicParameter = TransmitPublicParameterBuilder.build(inputStream);
            FetchPublicParameterResponsePacket responsePacket = new FetchPublicParameterResponsePacket();
            responsePacket.setTransmitPublicParameter(transmitPublicParameter);
            ctx.writeAndFlush(responsePacket);
        }catch (Exception e){
            log.error("公共参数文件不存在......");
        }finally {
            try {
                if (inputStream != null){
                    inputStream.close();
                }
            }catch (Exception e){
                log.error("文件关闭失败......");
            }
        }
    }
}
