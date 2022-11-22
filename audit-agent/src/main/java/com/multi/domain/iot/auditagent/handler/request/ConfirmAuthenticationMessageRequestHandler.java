package com.multi.domain.iot.auditagent.handler.request;

import com.multi.domain.iot.auditagent.calculator.PIDCalculator;
import com.multi.domain.iot.auditagent.pool.UDConnectionPoolingFactory;
import com.multi.domain.iot.auditagent.session.ConfirmAuthenticationMessageSessionUtils;
import com.multi.domain.iot.common.protocol.request.AuditAgentReturnPIDRequestPacket;
import com.multi.domain.iot.common.protocol.request.ConfirmAuthenticationMessageRequestPacket;
import com.multi.domain.iot.common.protocol.response.ConfirmAuthenticationMessageResponsePacket;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;

import java.net.InetSocketAddress;
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
    private ConfirmAuthenticationMessageRequestHandler() {
    }

    public static final ConfirmAuthenticationMessageRequestHandler INSTANCE = new ConfirmAuthenticationMessageRequestHandler();

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ConfirmAuthenticationMessageRequestPacket requestPacket) throws Exception {
        byte[] identityProtectionInformation = requestPacket.getIdentityProtectionInformation();
        Integer verifierId = requestPacket.getId();
        byte[] confirmInformation = requestPacket.getConfirmInformation();
        int totalConfirmNumber = requestPacket.getTotalVerifiersNumber();
        String uid = Base64.encodeBase64String(identityProtectionInformation);
        boolean success = requestPacket.isSuccess();
        //要将PID返回给UD的地址
        InetSocketAddress udAddress = requestPacket.getUdAddress();
        //一个UD的只能被确认一次
        if (!ConfirmAuthenticationMessageSessionUtils.isFinishConfirm(uid)) {
            if (ConfirmAuthenticationMessageSessionUtils.isNotConfirm(uid)) {
                ConfirmAuthenticationMessageSessionUtils.bindSession(uid);
                //开启延迟任务，5s后检查消息是否完毕
                ctx.channel().eventLoop().schedule(() -> {
                    AuditAgentReturnPIDRequestPacket packet = null;
                    if (ConfirmAuthenticationMessageSessionUtils.isReceiveFinish(uid, totalConfirmNumber)) {
                        log.info("Confirm that the message is collected and start generating PID for the UD");
                        packet = PIDCalculator.calculatePIDPacket(uid, identityProtectionInformation);
                        log.info("The PID is generated and starts returning to UD");
                        packet.setSuccess(true);
                    } else {
                        log.info("Waiting for the confirmation message of the verifier timed out, and the generation of PID failed");
                        packet = new AuditAgentReturnPIDRequestPacket();
                        packet.setSuccess(false);
                        packet.setReason("There is a verifier verification failure or Waiting for the confirmation message of the verifier timed out, and the generation of PID failed");
                    }
                    ConfirmAuthenticationMessageSessionUtils.afterMath(uid);
                    //将消息发送到对应的UD
                    try {
                        UDConnectionPoolingFactory.INSTANCE.getChannel(udAddress).writeAndFlush(packet);
                    } catch (InterruptedException e) {
                        log.error("send message to UD failure");
                    }
                }, 5, TimeUnit.SECONDS);
            }
            ConfirmAuthenticationMessageSessionUtils.receiveOneConfirmMessage(uid, verifierId, confirmInformation, totalConfirmNumber, success);
        }
        ctx.writeAndFlush(new ConfirmAuthenticationMessageResponsePacket());
    }
}
