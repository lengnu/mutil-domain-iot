package com.multi.domain.iot.verifier.handler.request;

import com.multi.domain.iot.common.message.UDAuthenticationMessage;
import com.multi.domain.iot.common.protocol.Packet;
import com.multi.domain.iot.common.protocol.request.ConfirmAuthenticationMessageRequestPacket;
import com.multi.domain.iot.common.protocol.request.UDAuthenticationMessageRequestPacket;
import com.multi.domain.iot.common.util.ComputeUtils;
import com.multi.domain.iot.common.validator.Validator;
import com.multi.domain.iot.verifier.param.IDVerifierParamsFactory;
import com.multi.domain.iot.verifier.pool.AuditAgentConnectionPollingFactory;
import com.multi.domain.iot.verifier.session.LocalSharesSessionUtils;
import com.multi.domain.iot.verifier.validator.UDAuthenticationMessageValidator;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import it.unisa.dia.gas.jpbc.Field;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;

/**
 * @BelongsProject: Multi-Domain-IoT
 * @BelongsPackage: com.multi.domain.iot.handler
 * @Author: duwei
 * @Date: 2022/11/21 13:06
 * @Description: UD发送验证消息到ID-Verifier请求处理器
 */
@Slf4j
@ChannelHandler.Sharable
public class UDAuthenticationMessageRequestHandler extends SimpleChannelInboundHandler<UDAuthenticationMessageRequestPacket> {

    private UDAuthenticationMessageRequestHandler() {

    }

    public static final UDAuthenticationMessageRequestHandler INSTANCE = new UDAuthenticationMessageRequestHandler();

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, UDAuthenticationMessageRequestPacket requestPacket) throws Exception {
        log.info("Listen to the authentication information transmitted by the UD");
        log.info("Start verifying the authentication information");
        Validator validator = new UDAuthenticationMessageValidator(requestPacket.getUdAuthenticationMessage());
        boolean verify = validator.verify();
        Packet confirmMessagePacket = generateConfirmMessagePacket(verify, requestPacket.getUdAuthenticationMessage());
        log.info("Send a confirmation message to auditAgent");
        AuditAgentConnectionPollingFactory.INSTANCE.getChannel(
                new InetSocketAddress(
                        IDVerifierParamsFactory.getInstance().getAuditAgentSession().getHost(),
                        IDVerifierParamsFactory.getInstance().getAuditAgentSession().getListenPort()
                )
        ).writeAndFlush(confirmMessagePacket);
    }

    //生成确认消息包
    private Packet generateConfirmMessagePacket(boolean verify,UDAuthenticationMessage udAuthenticationMessage){
        ConfirmAuthenticationMessageRequestPacket request = new ConfirmAuthenticationMessageRequestPacket();
        request.setSuccess(verify);
        request.setTotalVerifiersNumber(udAuthenticationMessage.getTotalVerifiersNumber());
        request.setId(IDVerifierParamsFactory.getInstance().getId());
        request.setUdAddress(udAuthenticationMessage.getUdAddress());
        request.setIdentityProtectionInformation(udAuthenticationMessage.getIdentityProtectionInformation());
        request.setDomain(udAuthenticationMessage.getDomain());
        if (verify){
            LocalSharesSessionUtils.bindSession(udAuthenticationMessage);
            log.info("Authentication successful!");
            byte[] confirmMessage = computeConfirmMessage(udAuthenticationMessage);
            request.setConfirmInformation(confirmMessage);
        }else {
            log.error("Authentication failed!");
        }
        return request;
    }

    /**
     * 计算向auditAgent发送的确认消息
     */
    private byte[] computeConfirmMessage(UDAuthenticationMessage udAuthenticationMessage) {
        return computeConfirmMessage(
                udAuthenticationMessage.getPublicKeySharesProtection().get(IDVerifierParamsFactory.getInstance().getId()),
                IDVerifierParamsFactory.getInstance().getXi().toBytes(),
                IDVerifierParamsFactory.getInstance().getZq());
    }

    private byte[] computeConfirmMessage(byte[] publicKeyShareProtection, byte[] privateKey, Field Zq) {
        byte[] bytes = ComputeUtils.concatByteArray(publicKeyShareProtection, privateKey);
        return ComputeUtils.hashMessageToZq(bytes, Zq).toBytes();
    }
}
