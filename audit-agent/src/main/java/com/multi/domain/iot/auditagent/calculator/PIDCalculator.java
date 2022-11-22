package com.multi.domain.iot.auditagent.calculator;

import com.multi.domain.iot.auditagent.param.AuditAgentParams;
import com.multi.domain.iot.auditagent.param.AuditAgentParamsFactory;
import com.multi.domain.iot.auditagent.session.ConfirmAuthenticationMessageSessionUtils;
import com.multi.domain.iot.common.protocol.request.AuditAgentReturnPIDRequestPacket;
import com.multi.domain.iot.common.protocol.request.ConfirmAuthenticationMessageRequestPacket;
import com.multi.domain.iot.common.util.ComputeUtils;
import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Field;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;


import java.util.Collection;

/**
 * @author duwei
 * @version 1.0.0
 * @create 2022-11-22 20:33
 * @description UD的身份PID计算器
 */
@Slf4j
public class PIDCalculator {
    /**
     * 计算返回给UD的PID及验证信息
     */
    public static AuditAgentReturnPIDRequestPacket calculatePIDPacket(
            String uid, byte[] identityProtectionInformation) {
        AuditAgentParams auditAgentParams = AuditAgentParamsFactory.getInstance();
        Field G = auditAgentParams.getG1();
        Element g = auditAgentParams.getGeneratorOne();
        Field Zq = auditAgentParams.getZq();
        Element msk = auditAgentParams.getMsk().getImmutable();

        Collection<byte[]> confirmsInformation = ConfirmAuthenticationMessageSessionUtils.getConfirmsInformation(uid);
        Element alpha = ComputeUtils.calculateAccumulation(confirmsInformation, Zq).getImmutable();
        Element beta = Zq.newRandomElement().getImmutable();
        Element exponential = alpha.mul(
                beta.add(ComputeUtils.hashMessageToZq(identityProtectionInformation, Zq)).getImmutable()
        ).getImmutable();
        //1.计算PID
        Element PID = g.powZn(exponential).getImmutable();
        //2.计算hash
        Element hash = ComputeUtils.H3(PID,Zq);
        //3.计算sign
        Element back = hash.mulZn(msk).getImmutable();
        Element sign = exponential.add(back).getImmutable();
        AuditAgentReturnPIDRequestPacket packet = new AuditAgentReturnPIDRequestPacket();
        packet.setPID(PID.toBytes());
        packet.setHash(hash.toBytes());
        packet.setSign(sign.toBytes());
        return packet;
    }
}
