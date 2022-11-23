package com.multi.domain.iot.ud.handler.request;

import com.multi.domain.iot.common.protocol.request.AuditAgentReturnPIDRequestPacket;
import com.multi.domain.iot.common.protocol.response.AuditAgentReturnPIDResponsePacket;
import com.multi.domain.iot.common.validator.Validator;
import com.multi.domain.iot.ud.param.UDParamsFactory;
import com.multi.domain.iot.ud.validator.PIDValidator;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;

/**
 * @author duwei
 * @version 1.0.0
 * @create 2022-11-22 21:35
 * @description 处理AA返回的PID，需要对其进行校验
 */
@Slf4j
@ChannelHandler.Sharable
public class AuditAgentReturnPIDRequestHandler extends SimpleChannelInboundHandler<AuditAgentReturnPIDRequestPacket> {
    private AuditAgentReturnPIDRequestHandler(){}

    public static final AuditAgentReturnPIDRequestHandler INSTANCE = new AuditAgentReturnPIDRequestHandler();
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, AuditAgentReturnPIDRequestPacket requestPacket) throws Exception {
        log.info("The PID and signature information returned by auditAgent are detected");
        if (requestPacket.isSuccess()){
            log.info("AuditAgent generates PID successfully, and starts to verify");
            Validator validator = new PIDValidator(requestPacket);
            boolean success = validator.verify();
            if (success){
                log.info("UD successfully verifies and accepts PID as his fake identity");
                log.info("PID(Base64) : {} " , Base64.encodeBase64String(requestPacket.getPID()));
                UDParamsFactory.getInstance().setPID(requestPacket.getPID());
                UDParamsFactory.getInstance().writePIDToFile(UDParamsFactory.SELF_PARAMS_SAVE_PATH);
                log.info("PID has been written to file {}",UDParamsFactory.SELF_PARAMS_SAVE_PATH);
                log.info("Notify auditAgent to deposit the information into the blockchain");
                AuditAgentReturnPIDResponsePacket responsePacket = new AuditAgentReturnPIDResponsePacket();
                responsePacket.setPID(responsePacket.getPID());
                responsePacket.setDomain(UDParamsFactory.getInstance().getDomain());
                responsePacket.setIdentityProtectionInformation(UDParamsFactory.getInstance().getIdentityProtectionInformation());
                ctx.writeAndFlush(responsePacket);
            }else {
                log.error("Validation failed and the program is about to exit");
                System.exit(1);
            }
        }else {
            log.error("AA failed to generate PID, and the program is ready to exit");
        }
    }
}
