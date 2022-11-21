package com.multi.domain.iot.auditagent.param;

import com.multi.domain.iot.common.domain.Domain;
import com.multi.domain.iot.common.param.PublicParams;
import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Field;
import it.unisa.dia.gas.jpbc.Pairing;
import it.unisa.dia.gas.plaf.jpbc.pairing.PairingFactory;
import lombok.Data;
import org.apache.commons.codec.binary.Base64;

import java.util.Properties;

/**
 * @BelongsProject: Multi-Domain-IoT
 * @BelongsPackage: com.multi.domain.iot.param
 * @Author: duwei
 * @Date: 2022/11/17 17:04
 * @Description: 审计代理的本地参数
 */
@Data
public class AuditAgentParams extends PublicParams {
    private Pairing pairing;
    private Field Zq;
    private Field G1;
    private Element generatorOne;
    private Element generatorTwo;
    private Element msk;
    private Element publicKey;
    private String publicParamSavePath;
    private String selfParamSavePath;
    private Integer id;
    //监听的端口
    private int listenPort;
    //主机IP
    private String host;

    protected AuditAgentParams(PublicParams publicParams, String publicParamSavePath, String selfParamSavePath) {
        this.setCurvesType(publicParams.getCurvesType());
        this.setExp1(publicParams.getExp1());
        this.setExp2(publicParams.getExp2());
        this.setGeneratorOneBase64(publicParams.getGeneratorOneBase64());
        this.setGeneratorTwoBase64(publicParams.getGeneratorTwoBase64());
        this.setH(publicParams.getH());
        this.setQ(publicParams.getQ());
        this.setR(publicParams.getR());
        this.setSign0(publicParams.getSign0());
        this.setSign1(publicParams.getSign1());
        this.setInitialize(publicParams.getInitialize());
        this.setSecureParameter(publicParams.getSecureParameter());
        this.publicParamSavePath = publicParamSavePath;
        this.selfParamSavePath = selfParamSavePath;
        this.init(publicParams, this.publicParamSavePath, this.selfParamSavePath);
    }


    @Override
    protected void autowireSelfParamsToProperties(Properties properties) {
        properties.setProperty(Constant.MSK, Base64.encodeBase64String(this.msk.toBytes()));
        properties.setProperty(Constant.PUBLIC_KEY, Base64.encodeBase64String(this.publicKey.toBytes()));
        properties.setProperty(Constant.ID, String.valueOf(this.id));
        properties.setProperty(Constant.HOST, this.host);
        properties.setProperty(Constant.LISTEN_PORT, String.valueOf(this.listenPort));
    }


    /**
     * 初始化方法
     *
     * @param publicParams
     * @param publicParamSavePath
     * @param selfParamSavePath
     * @return
     */
    public void init(PublicParams publicParams, String publicParamSavePath, String selfParamSavePath) {
        this.setPublicParamSavePath(publicParamSavePath);
        this.setSelfParamSavePath(selfParamSavePath);
        //写人公共参数到文件，方便初始化双线性对
        publicParams.writePublicParamsToFile(publicParamSavePath);
        Pairing pairing = PairingFactory.getPairing(this.publicParamSavePath);
        this.setG1(pairing.getG1());
        this.setZq(pairing.getZr());
        this.setPairing(pairing);
        this.setGeneratorOne(this.G1.newElementFromBytes(
                Base64.decodeBase64(publicParams.getGeneratorOneBase64())
        ).getImmutable());
        this.setGeneratorTwo(this.G1.newElementFromBytes(
                Base64.decodeBase64(publicParams.getGeneratorTwoBase64())
        ).getImmutable());
        this.setMsk(this.getZq().newRandomElement().getImmutable());
        this.setPublicKey(this.getGeneratorOne().powZn(this.getMsk()).getImmutable());
    }


    @Override
    public String toString() {
        return "AuditAgentParams{\n" +
                "generatorOne=" + generatorOne +
                ",\n generatorTwo=" + generatorTwo +
                ",\n msk=" + msk +
                ",\n publicKey=" + publicKey +
                ",\n curvesType='" + curvesType + '\'' +
                ",\n q='" + q + '\'' +
                ",\n h='" + h + '\'' +
                ",\n r='" + r + '\'' +
                ",\n exp2='" + exp2 + '\'' +
                ",\n exp1='" + exp1 + '\'' +
                ",\n sign1='" + sign1 + '\'' +
                ",\n sign0='" + sign0 + '\'' +
                ",\n secureParameter=" + secureParameter +
                ",\n id=" + id +
                "\n}";
    }
}
