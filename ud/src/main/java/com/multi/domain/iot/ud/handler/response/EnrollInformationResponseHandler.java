//package com.multi.domain.iot.ud.handler.response;
//
//import com.multi.domain.iot.ud.param.UDParamsFactory;
//import com.multi.domain.iot.common.protocol.response.EnrollInformationResponsePacket;
//import com.multi.domain.iot.ud.param.UDParams;
//import io.netty.channel.ChannelHandlerContext;
//import io.netty.channel.SimpleChannelInboundHandler;
//import lombok.extern.slf4j.Slf4j;
//
///**
// * @author duwei
// * @version 1.0.0
// * @create 2022-11-19 19:37
// * @description 注册信息响应
// */
//@Slf4j
//public class EnrollInformationResponseHandler extends SimpleChannelInboundHandler<EnrollInformationResponsePacket> {
//   private EnrollInformationResponseHandler(){
//
//   }
//
//   public static final EnrollInformationResponseHandler INSTANCE = new EnrollInformationResponseHandler();
//
//    @Override
//    protected void channelRead0(ChannelHandlerContext ctx, EnrollInformationResponsePacket responsePacket) throws Exception {
//        if (responsePacket.isSuccess()){
//
//            UDParams udParams = UDParamsFactory.getInstance();
//            udParams.setId(responsePacket.getId());
//            udParams.writeSelfParamsToFile(UDParamsFactory.SELF_PARAMS_SAVE_PATH);
//            log.info("The ud self params are already saved in the file : {} ",UDParamsFactory.SELF_PARAMS_SAVE_PATH);
//            log.info("ud : \n{}",udParams);
//            log.info("Information registered successfully, current ud ID : {}",responsePacket.getId());
//            log.info("Information registered successfully, current ud ID : {}",responsePacket.getId());
//
//        }else {
//            log.error("A location error has occurred and is exiting......");
//            System.exit(1);
//        }
//    }
//}
