package com.multi.domain.iot.param;

import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Field;
import it.unisa.dia.gas.jpbc.Pairing;
import it.unisa.dia.gas.plaf.jpbc.field.z.ZElement;
import it.unisa.dia.gas.plaf.jpbc.field.z.ZrField;
import it.unisa.dia.gas.plaf.jpbc.pairing.PairingFactory;
import lombok.Data;
import org.apache.commons.codec.binary.Base64;

/**
 * @BelongsProject: Multi-Domain-IoT
 * @BelongsPackage: com.multi.domain.iot.param
 * @Author: duwei
 * @Date: 2022/11/17 17:04
 * @Description: 审计代理的本地参数
 */
@Data
public class AuditAgentParams {
    private PublicParams publicParams;
    private Pairing pairing;
    private Field Zq;
    private Field G1;
    private Element generatorOne;
    private Element generatorTwo;
    private Element msk;
    private Element publicKey;
    private String paramSavePath;

    private AuditAgentParams() {

    }

    public static AuditAgentParams build(PublicParams publicParams, String paramSavePath) {
        AuditAgentParams params = new AuditAgentParams();
        params.setPublicParams(publicParams);
        params.setParamSavePath(paramSavePath);
        publicParams.writePublicParamsToFile(paramSavePath);
        Pairing pairing = PairingFactory.getPairing(params.paramSavePath);
        params.setG1(pairing.getG1());
        params.setZq(pairing.getZr());
        params.setPairing(pairing);
        params.setGeneratorOne(params.G1.newElementFromBytes(
                Base64.decodeBase64(publicParams.getGeneratorOneBase64())
        ).getImmutable());
        params.setGeneratorTwo(params.G1.newElementFromBytes(
                Base64.decodeBase64(publicParams.getGeneratorTwoBase64())
        ).getImmutable());
        params.setMsk(params.getZq().newRandomElement().getImmutable());
        params.setPublicKey(params.getGeneratorOne().powZn(params.getMsk()).getImmutable());
        return params;
    }

    @Override
    public String toString() {
        return "AuditAgentParams{" +
                "publicParams=" + publicParams +
                "\n generatorOne=" + generatorOne +
                "\n generatorTwo=" + generatorTwo +
                "\n msk=" + msk +
                "\n publicKey=" + publicKey +
                "\n paramSavePath='" + paramSavePath + '\'' +
                '}';
    }
}
