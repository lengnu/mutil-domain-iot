package com.multi.domain.iot.verifier.handler.response;

import com.multi.domain.iot.verifier.param.IDVerifierParams;
import com.multi.domain.iot.verifier.param.IDVerifierParamsFactory;
import com.multi.domain.iot.common.protocol.response.EnrollInformationResponsePacket;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * @author duwei
 * @version 1.0.0
 * @create 2022-11-19 19:37
 * @description 注册信息响应
 */
@Slf4j
public class EnrollInformationResponseHandler extends SimpleChannelInboundHandler<EnrollInformationResponsePacket> {
   private EnrollInformationResponseHandler(){

   }

   public static final EnrollInformationResponseHandler INSTANCE = new EnrollInformationResponseHandler();

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, EnrollInformationResponsePacket responsePacket) throws Exception {
        if (responsePacket.isSuccess()){
            log.info("The id-Verifier registers its public key and address information successfully");
            IDVerifierParams idVerifierParams = IDVerifierParamsFactory.getInstance();
            idVerifierParams.setId(responsePacket.getId());
            idVerifierParams.writeSelfParamsToFile(IDVerifierParamsFactory.SELF_PARAMS_SAVE_PATH);
            log.info("The id-Verifier self params are already saved in the file : {} ",IDVerifierParamsFactory.SELF_PARAMS_SAVE_PATH);
        }else {
            log.error(responsePacket.getReason());
            System.exit(1);
        }
    }
}
