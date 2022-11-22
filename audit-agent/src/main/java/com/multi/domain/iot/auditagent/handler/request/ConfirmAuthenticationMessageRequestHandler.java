package com.multi.domain.iot.auditagent.handler.request;

import com.multi.domain.iot.auditagent.session.ConfirmAuthenticationMessageSessionUtils;
import com.multi.domain.iot.common.protocol.request.ConfirmAuthenticationMessageRequestPacket;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;

import java.util.concurrent.TimeUnit;

/**
 * @BelongsProject: Multi-Domain-IoT
 * @BelongsPackage: com.multi.domain.iot.auditagent.handler.request
 * @Author: duwei
 * @Date: 2022/11/22 15:43
 * @Description: 处理来自IDV的验证消息
 */
@Slf4j
@ChannelHandler.Sharable
public class ConfirmAuthenticationMessageRequestHandler extends SimpleChannelInboundHandler<ConfirmAuthenticationMessageRequestPacket> {
    private ConfirmAuthenticationMessageRequestHandler(){}

    public static final ConfirmAuthenticationMessageRequestHandler INSTANCE = new ConfirmAuthenticationMessageRequestHandler();

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ConfirmAuthenticationMessageRequestPacket requestPacket) throws Exception {
        byte[] identityProtectionInformation = requestPacket.getIdentityProtectionInformation();
        Integer verifierId = requestPacket.getId();
        byte[] confirmInformation = requestPacket.getConfirmInformation();
        int totalConfirmNumber = requestPacket.getTotalVerifiersNumber();
        String uid = Base64.encodeBase64String(identityProtectionInformation);
        boolean success = requestPacket.isSuccess();
        //一个UD的只能被确认一次
        if (!ConfirmAuthenticationMessageSessionUtils.isFinishConfirm(uid)){
            if (ConfirmAuthenticationMessageSessionUtils.isNotConfirm(uid)){
                ConfirmAuthenticationMessageSessionUtils.bindSession(uid);
                //开启延迟任务，5s后检查消息是否完毕
                ctx.channel().eventLoop().schedule(() -> {
                    if (ConfirmAuthenticationMessageSessionUtils.isFinishConfirm(uid)){
                        System.out.println("tttt");
                        ConfirmAuthenticationMessageSessionUtils.afterMath(uid);
                    }else {
                        System.out.println("fffff");
                    }
                },5, TimeUnit.SECONDS);
            }
            ConfirmAuthenticationMessageSessionUtils.receiveOneConfirmMessage(uid,verifierId,confirmInformation,totalConfirmNumber,success);
        }





    }
}
