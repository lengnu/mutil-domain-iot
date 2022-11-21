package com.multi.domain.iot.verifier.handler.request;

import com.multi.domain.iot.common.entity.UDAuthenticationMessage;
import com.multi.domain.iot.common.protocol.request.UDAuthenticationMessageRequestPacket;
import com.multi.domain.iot.verifier.param.IDVerifierParams;
import com.multi.domain.iot.verifier.param.IDVerifierParamsFactory;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Field;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

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
        log.info("Listen to the authentication information transmitted by the UD and start validating it");
        UDAuthenticationMessage udAuthenticationMessage = requestPacket.getUdAuthenticationMessage();
        System.out.println("hahahahahhaha");
        //verify(udAuthenticationMessage);
    }

    private boolean verify(UDAuthenticationMessage udAuthenticationMessage){
        Map<Integer, byte[]> sharesCommitment = udAuthenticationMessage.getSharesCommitment();
        Map<Integer, byte[]> polynomialCoefficientCommitment = udAuthenticationMessage.getPublicKeySharesProtection();
        int id = 2;
        IDVerifierParams idVerifierParams = IDVerifierParamsFactory.getInstance();
        Field G1 = idVerifierParams.getG1();
        Field Zq = idVerifierParams.getZq();
        Element element = computeTemp(Zq.newElement(id).getImmutable(), polynomialCoefficientCommitment, 3);
        System.out.println("---" + element.equals(G1.newElementFromBytes(sharesCommitment.get(id))));
        return true;
    }

//    private boolean verifyShares(){
//
//    }

    private Element computeTemp(Element i, Map<Integer,byte[]>  polynomialCoefficientCommitment,int t){
        IDVerifierParams idVerifierParams = IDVerifierParamsFactory.getInstance();
        Field G1 = idVerifierParams.getG1();
        Field Zq = idVerifierParams.getZq();
        Element result = G1.newOneElement();
        for (Map.Entry<Integer,byte[]> coefficientCommitment : polynomialCoefficientCommitment.entrySet()){
            Integer coefficientIndex = coefficientCommitment.getKey();
            byte[] coefficientCommitmentValue = coefficientCommitment.getValue();
            Element coefficientCommitmentValueElement = Zq.newElementFromBytes(coefficientCommitmentValue);
            Element temp = i.powZn(Zq.newElement(coefficientCommitmentValueElement));
            Element C_k = G1.newElementFromBytes(coefficientCommitmentValue).getImmutable();
            result.mul(C_k);
        }
        return result;
    }
}
