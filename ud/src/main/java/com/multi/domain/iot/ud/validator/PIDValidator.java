package com.multi.domain.iot.ud.validator;

import com.multi.domain.iot.common.protocol.request.AuditAgentReturnPIDRequestPacket;
import com.multi.domain.iot.common.util.ComputeUtils;
import com.multi.domain.iot.common.validator.Validator;
import com.multi.domain.iot.ud.param.UDParams;
import com.multi.domain.iot.ud.param.UDParamsFactory;
import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Field;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;

import java.nio.channels.Pipe;


/**
 * @author duwei
 * @version 1.0.0
 * @create 2022-11-22 22:12
 * @description PID验证器
 */
@Slf4j
public class PIDValidator implements Validator {
    private AuditAgentReturnPIDRequestPacket requestPacket;

    public PIDValidator(AuditAgentReturnPIDRequestPacket requestPacket) {
        this.requestPacket = requestPacket;
    }

    @Override
    public boolean verify() {
        UDParams udParams = UDParamsFactory.getInstance();
        Field G = udParams.getG1();
        Field Zq = udParams.getZq();
        Element g = udParams.getGeneratorOne();
        byte[] publicKey = udParams.getAuditAgentSession().getPublicKey();
        Element pkM = G.newElementFromBytes(publicKey).getImmutable();
        return verify(Zq, G, pkM, g);
    }

    private boolean verify(Field Zq, Field G, Element pkM, Element g) {
        Element PID = G.newElementFromBytes(this.requestPacket.getPID()).getImmutable();
        Element hash = Zq.newElementFromBytes(this.requestPacket.getHash()).getImmutable();
        Element sign = Zq.newElementFromBytes(this.requestPacket.getSign()).getImmutable();
        Element hashNew = ComputeUtils.H3(PID, Zq).getImmutable();
        return verifyHash(hash, hashNew) &&
                verifySign(g, hashNew, pkM, PID, sign);
    }

    private boolean verifyHash(Element hash, Element hashNew) {
        log.info("Start verifying the hash");
        boolean success = hashNew.equals(hash);
        if (success) {
            log.info("Hash validation passed");
        } else {
            log.error("Hash validation failed");
        }
        return success;
    }

    private boolean verifySign(Element g,
                               Element hashNew,
                               Element pkM,
                               Element PID,
                               Element sign) {
        log.info("Start verifying the sign");
        Element front = pkM.powZn(hashNew).getImmutable();
        Element right = front.mul(PID);
        Element left = g.powZn(sign);
        boolean success = right.equals(left);
        if (success) {
            log.info("Sign validation passed");
        } else {
            log.error("Sign validation failed");
        }
        return success;
    }
}
