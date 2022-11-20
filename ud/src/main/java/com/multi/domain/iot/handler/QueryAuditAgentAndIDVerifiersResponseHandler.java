package com.multi.domain.iot.handler;

import com.multi.domain.iot.param.UDParamsFactory;
import com.multi.domain.iot.protocol.response.QueryAuditAgentAndIDVerifiersResponsePacket;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;

/**
 * @author duwei
 * @version 1.0.0
 * @create 2022-11-20 21:59
 * @description 查询AA和IDV在服务器的注册响应处理器
 */
@Slf4j
public class QueryAuditAgentAndIDVerifiersResponseHandler extends SimpleChannelInboundHandler<QueryAuditAgentAndIDVerifiersResponsePacket> {
    private QueryAuditAgentAndIDVerifiersResponseHandler() {

    }

    public static final QueryAuditAgentAndIDVerifiersResponseHandler INSTANCE = new QueryAuditAgentAndIDVerifiersResponseHandler();

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, QueryAuditAgentAndIDVerifiersResponsePacket responsePacket) throws Exception {
        log.info("Query the auditAgent and IDVerifiers information: ");
        responsePacket.getRoleInformationMap().forEach((role, informationMap) -> {
            log.info("role : {} ", role.getRole());
            informationMap.forEach((id, information) -> {
                log.info("id : {},ip : {},listenPort : {}", id, information.getIp(),
                        information.getListenPort());
            });
        });


        log.info("Construct real identity information......");
        byte[] information = constructIdentityInformation("杜伟", 2, "231@qq.com", "232");
        log.info("Construct real identity information finished......");


    }

    /**
     * 构造身份信息字节数组，字节数组长度为 secureParam * 4 / 8
     *
     * @param name
     * @param id
     * @param email
     * @param phone
     * @return
     */
    private byte[] constructIdentityInformation(String name,
                                                int id,
                                                String email,
                                                String phone) {
        int identityInformationLength = UDParamsFactory.getInstance().getSecureParameter();
        byte[] identityInformation = new byte[identityInformationLength];

        //单条信息的长度
        int singleInformationLength = identityInformationLength / 4;
        byte[] nameBytes = constructSingleInformation(name, singleInformationLength);
        byte[] idBytes = constructSingleInformation(String.valueOf(id), singleInformationLength);
        byte[] emailBytes = constructSingleInformation(email, singleInformationLength);
        byte[] phoneBytes = constructSingleInformation(phone, singleInformationLength);
        System.arraycopy(nameBytes, 0, identityInformation, 0, singleInformationLength);
        System.arraycopy(idBytes, 0, identityInformation, singleInformationLength, singleInformationLength);
        System.arraycopy(emailBytes, 0, identityInformation, 2 * singleInformationLength, singleInformationLength);
        System.arraycopy(phoneBytes, 0, identityInformation, 3 * singleInformationLength, singleInformationLength);
//        System.out.println("name len : " + nameBytes.length + "\t bytes : " + Arrays.toString(nameBytes));
//        System.out.println("id len : " + idBytes.length + "\t bytes : " + Arrays.toString(idBytes));
//        System.out.println("email len : " + emailBytes.length+ "\t bytes : " + Arrays.toString(emailBytes));
//        System.out.println("phone len : " + phoneBytes.length+ "\t bytes : " + Arrays.toString(phoneBytes));
//        System.out.println("identity len : " + identityInformation.length+ "\t bytes : " + Arrays.toString(identityInformation));
        return identityInformation;
    }

    /**
     * 根据信息构造字节数组，长度限制为singleInformation，不足高位补0,高于前面截取
     *
     * @param singleInformation
     * @param singleInformationLength
     * @return
     */
    private byte[] constructSingleInformation(String singleInformation, int singleInformationLength) {
        byte[] singleInformationBytes = singleInformation.getBytes();
        return Arrays.copyOf(singleInformationBytes, singleInformationLength);
    }
}
