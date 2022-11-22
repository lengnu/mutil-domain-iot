package com.multi.domain.iot.ud.param;

import com.multi.domain.iot.common.domain.Domain;
import com.multi.domain.iot.common.message.UDAuthenticationMessage;
import com.multi.domain.iot.common.param.PublicParams;
import com.multi.domain.iot.common.polynomial.Polynomial;
import com.multi.domain.iot.common.protocol.response.QueryAuditAgentAndIDVerifiersResponsePacket;
import com.multi.domain.iot.common.session.Session;
import com.multi.domain.iot.common.util.ComputeUtils;
import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Field;
import it.unisa.dia.gas.jpbc.Pairing;
import it.unisa.dia.gas.plaf.jpbc.pairing.PairingFactory;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;

import java.io.*;
import java.net.InetSocketAddress;
import java.util.*;

/**
 * @BelongsProject: Multi-Domain-IoT
 * @BelongsPackage: com.multi.domain.iot.param
 * @Author: duwei
 * @Date: 2022/11/17 17:04
 * @Description: UD的本地参数
 */
@Data
@Slf4j
public class UDParams extends PublicParams {
    private Pairing pairing;
    private Field Zq;
    private Field G1;
    private Element generatorOne;
    private Element generatorTwo;
    private String publicParamSavePath;
    private String selfParamSavePath;

    private Domain domain;
    private int listenPort;
    private String host;

    private String name;
    private String email;
    private Integer id;
    private String phone;
    //秘密值
    private Element s;
    //秘密
    private Element secret;
    //身份信息
    private byte[] identityInformation;
    //身份保护信息
    private byte[] identityProtectionInformation;
    //用户本地的多项式
    private Polynomial polynomial;
    //存储UD的所有验证信息
    private UDAuthenticationMessage udAuthenticationMessage = new UDAuthenticationMessage();
    //为每个身份验证者的份额
    private Map<Integer, byte[]> shares;

    //储存该域中验证者的信息
    private Map<Integer, Session> verifiersSession = new HashMap<>();
    //审计代理的信息
    private Session auditAgentSession;
    //储存该域中验证者的公钥信息
    private Map<Integer, byte[]> verifiersPublicKey = new HashMap<>();
    private Map<Integer, InetSocketAddress> verifiersAddress = new HashMap<>();

    //假名
    private byte[] PID;

    /**
     * 将系统中升级代理和请求域的验证者信息存储到本地
     */
    public void saveOtherInformation(QueryAuditAgentAndIDVerifiersResponsePacket responsePacket) {
        log.info("Store the public key and address of the auditAgent and ID-Verifiers locally");
        this.auditAgentSession = responsePacket.getAuditAgentSession();
        this.verifiersSession = responsePacket.getIdVerifierSession();
        this.verifiersSession.forEach((id, session) -> {
            this.verifiersPublicKey.put(id, session.getPublicKey());
            this.verifiersAddress.put(id, new InetSocketAddress(session.getHost(), session.getListenPort()));
        });
    }

    /**
     * 生成所有的认证信息,需要传入多项式的阶，默认阶为所有验证者数量的一半
     */
    public void generateAuthorizeInformation() {
        this.generateAuthorizeInformation(this.verifiersSession.size() / 2);
    }


    private void generateAuthorizeInformation(int polynomialOrder) {
        //1.计算身份信息
        computeIdentityInformation();
        //2.计算真实身份保护信息
        computeIdentityProtectionInformation();
        //3.构建随机多项式
        computeRandomPolynomial(polynomialOrder);
        //4.为每个身份验证者计算多项式份额
        computeShares(this.verifiersPublicKey.keySet());
        //5.计算对多项式系数的承诺
        computePolynomialCoefficientsCommitment();
        //6.计算对于验证者每个份额的承诺
        computeSharesCommitment();
        //7.生成计算保护,用公钥对份额进行保护
        computePublicKeySharesProtection();
        //8.计算验证信息
        computeVerifiersInformation();
        //9.设置总的验证者数量
        this.udAuthenticationMessage.setTotalVerifiersNumber(this.verifiersSession.size());
        //10.设置用户的唯一标识-用其身份隐私保护信息生成
        this.udAuthenticationMessage.setUdAddress(new InetSocketAddress(this.host,this.listenPort));
    }

    private void computeRandomPolynomial(int order) {
        log.info("Construct random polynomial");
        this.polynomial = new Polynomial(order, this.getZq(), this.G1, this.s);
    }

    /**
     * 计算多项式份额
     */
    private void computeShares(Set<Integer> ids) {
        log.info("Calculate shares are calculated for each IDVerify");
        this.shares = this.polynomial.computeShares(ids);
    }

    /**
     * 计算系数的承诺
     */
    private void computePolynomialCoefficientsCommitment() {
        log.info("Calculate the polynomial coefficients commitment");
        Map<Integer, byte[]> coefficientsCommitment = this.polynomial.computePolynomialCoefficientsCommitment(this.generatorTwo);
        this.udAuthenticationMessage.setPolynomialCoefficientsCommitment(coefficientsCommitment);
    }

    /**
     * 计算份额的承诺
     */
    private void computeSharesCommitment() {
        log.info("Calculate the shares commitment");
        Map<Integer, byte[]> sharesCommitment = this.polynomial.computeSharesCommitment(this.shares, this.generatorTwo);
        this.udAuthenticationMessage.setSharesCommitment(sharesCommitment);
    }

    /**
     * 计算公钥份额保护
     */
    private void computePublicKeySharesProtection() {
        log.info("Calculate the publicKey shares protection");
        Map<Integer, byte[]> publicKeySharesProtection = this.polynomial.computePublicKeySharesProtection(this.verifiersPublicKey, this.shares);
        this.udAuthenticationMessage.setPublicKeySharesProtection(publicKeySharesProtection);
    }

    /**
     * 计算双线性对的验证信息
     */
    private void computeVerifiersInformation() {
        log.info("Calculate the pairing verifiers information");
        Map<Integer, byte[]> verifiesInformation = this.computeVerifiersInformation(
                this.udAuthenticationMessage.getSharesCommitment(),
                this.verifiersPublicKey
        );
        this.udAuthenticationMessage.setVerifyInformation(verifiesInformation);
    }

    private Map<Integer, byte[]> computeVerifiersInformation(
            Map<Integer, byte[]> sharesCommitment,
            Map<Integer, byte[]> publicKeys) {
        Map<Integer, byte[]> result = new HashMap<>();
        sharesCommitment.forEach((id, share) -> {
            byte[] publicKey = publicKeys.get(id);
            Element verifyInformation = computeVerifyInformation(share, publicKey);
            result.put(id, verifyInformation.toBytes());
        });
        return result;
    }

    private Element computeVerifyInformation(byte[] shareCommitment,
                                             byte[] publicKey) {
        Element shareCommitmentElement = this.G1.newElementFromBytes(shareCommitment).getImmutable();
        Element publicKeyElement = this.G1.newElementFromBytes(publicKey).getImmutable();
        return this.pairing.pairing(shareCommitmentElement, publicKeyElement);
    }


    /**
     * 计算身份保护信息
     */
    private void computeIdentityProtectionInformation() {
        log.info("Calculate identity protection information");
        this.identityProtectionInformation = ComputeUtils.xor(this.identityInformation, ComputeUtils.sha512(this.secret.toBytes()));
        this.udAuthenticationMessage.setIdentityProtectionInformation(this.identityProtectionInformation);
    }

    /**
     * 计算身份信息
     */
    private void computeIdentityInformation() {
        log.info("Calculate real identity information");
        this.identityInformation = constructIdentityInformation(this.name, this.id, this.email, this.phone, this.secureParameter * 4 / 8);
    }

    //将单条信息转换为数组
    private byte[] constructSingleIdentityInformation(String information, int targetLength) {
        return ComputeUtils.convertStringToFixLengthByteArray(information, targetLength);
    }

    /**
     * 构造身份信息字节数组，字节数组总长度为identityInformationLength
     */
    private byte[] constructIdentityInformation(String name,
                                                int id,
                                                String email,
                                                String phone,
                                                int identityInformationLength) {
        //单条信息的长度
        int singleInformationLength = identityInformationLength / 4;
        byte[] nameBytes = constructSingleIdentityInformation(name, singleInformationLength);
        byte[] idBytes = constructSingleIdentityInformation(String.valueOf(id), singleInformationLength);
        byte[] emailBytes = constructSingleIdentityInformation(email, singleInformationLength);
        byte[] phoneBytes = constructSingleIdentityInformation(phone, singleInformationLength);
        //将所有信息拼接在一起
        return ComputeUtils.concatByteArray(singleInformationLength, nameBytes, idBytes, emailBytes, phoneBytes);
    }


    @Override
    protected void autowireSelfParamsToProperties(Properties properties) {
        properties.setProperty(Constant.SECRET, Base64.encodeBase64String(this.secret.toBytes()));
        properties.setProperty(Constant.S, Base64.encodeBase64String(this.s.toBytes()));
        properties.setProperty(Constant.PHONE, this.phone);
        properties.setProperty(Constant.EMAIL, this.email);
        properties.setProperty(Constant.NAME, this.name);
        properties.setProperty(Constant.ID, String.valueOf(this.id));
        properties.setProperty(Constant.DOMAIN, this.domain.getDomain());
    }


    public UDParams(PublicParams publicParams, String publicParamSavePath, String selfParamSavePath) {
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


    /**
     * 初始化方法
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
        Element temp1 = this.G1.newElementFromBytes(Base64.decodeBase64(publicParams.getGeneratorOneBase64()));
        this.generatorOne = temp1.getImmutable();
        Element temp2 = this.G1.newElementFromBytes(Base64.decodeBase64(publicParams.getGeneratorTwoBase64()));
        this.generatorTwo = temp2.getImmutable();
        //初始化秘密
        this.s = this.Zq.newRandomElement().getImmutable();
        this.secret = this.generatorOne.powZn(this.s).getImmutable();
        //初始化身份信息
        this.name = "zhangsan";
        this.id = 2343;
        this.email = "12321.@qq.com";
        this.phone = "123324788345";
    }

    public void writePIDToFile(String filePath) throws IOException {
        OutputStream outputStream = new FileOutputStream(filePath,true);
        Properties properties = new Properties();
        properties.setProperty("PID",Base64.encodeBase64String(this.PID));
        properties.store(outputStream,"store PID");
    }


    @Override
    public String toString() {
        return "AuditAgentParams{\n" +
                "generatorOne=" + generatorOne +
                ",\n generatorTwo=" + generatorTwo +
                ",\n curvesType='" + curvesType + '\'' +
                ",\n q='" + q + '\'' +
                ",\n h='" + h + '\'' +
                ",\n r='" + r + '\'' +
                ",\n exp2='" + exp2 + '\'' +
                ",\n exp1='" + exp1 + '\'' +
                ",\n sign1='" + sign1 + '\'' +
                ",\n sign0='" + sign0 + '\'' +
                ",\n secureParameter=" + secureParameter +
                "\n}";
    }
}
