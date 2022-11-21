package com.multi.domain.iot.ud.param;

import com.multi.domain.iot.common.domain.Domain;
import com.multi.domain.iot.common.entity.UDAllVerifyInformation;
import com.multi.domain.iot.common.param.EnrollInformationOnPublicServer;
import com.multi.domain.iot.common.param.PublicParams;
import com.multi.domain.iot.common.polynomial.Polynomial;
import com.multi.domain.iot.common.protocol.response.QueryAuditAgentAndIDVerifiersResponsePacket;
import com.multi.domain.iot.common.role.Role;
import com.multi.domain.iot.common.session.Session;
import com.multi.domain.iot.common.util.ComputeUtils;
import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Field;
import it.unisa.dia.gas.jpbc.Pairing;
import it.unisa.dia.gas.plaf.jpbc.pairing.PairingFactory;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;

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
    private UDAllVerifyInformation udAllVerifyInformation = new UDAllVerifyInformation();
    //为每个身份验证者的份额
    private Map<Integer, byte[]> shares;

    //储存系统中不同域 验证者的信息
    private Map<Domain, Map<Integer, Session>> verifiersSession;
    //审计代理的信息
    private Session auditAgentSession;

    public void saveOtherInformation(QueryAuditAgentAndIDVerifiersResponsePacket responsePacket){
       this.auditAgentSession =  responsePacket.getAuditAgentSession();
    }


    //存储其他验证者的链接信息
    private Map<Integer, InetSocketAddress> verifiersConnectionsInformation;
    //存储审计代理的链接信息
    private Map<Integer, InetSocketAddress> auditAgentConnectionsInformation;

    private void ensureMapNotNull() {
       if (this.auditAgentPublicKey == null){
           this.auditAgentPublicKey = new HashMap<>();
       }
       if(this.auditAgentConnectionsInformation == null){
           this.auditAgentConnectionsInformation = new HashMap<>();
       }
       if (this.verifiersPublicKeys == null){
           this.verifiersPublicKeys = new HashMap<>();
       }
       if (this.verifiersConnectionsInformation == null){
           this.verifiersConnectionsInformation = new HashMap<>();
       }
    }

    /**
     * 将从服务器拿到其他人的公钥保和地址存在本地
     */
    public void saveOtherPublicKeyAndAddress(Map<Role, Map<Integer, EnrollInformationOnPublicServer>> otherInformationOnServer) {
        log.info("Store the public key and address of the auditAgent and ID-Verifiers locally");
        ensureMapNotNull();
        otherInformationOnServer.forEach((role, informationMap) -> {
            if (role == Role.AA) {
                informationMap.forEach((id, information) -> {
                    auditAgentPublicKey.put(id, information.getPublicKey());
                    auditAgentConnectionsInformation.


                            put(id, new InetSocketAddress(information.getHost(),
                            information.getListenPort()));
                });
            } else if (role == Role.IDV) {
                informationMap.forEach((id, information) -> {
                    verifiersPublicKeys.put(id, information.getPublicKey());
                    verifiersConnectionsInformation.put(id, new InetSocketAddress(information.getHost(),
                            information.getListenPort()));
                });
            }
        });
    }

    public void generateAllBoardCastInformation(int order) {
        computeIdentityInformation();
        computeIdentityProtectionInformation();
        this.polynomial = new Polynomial(order, this.Zq, this.s);
        constructRandomPolynomial(order);
        computeShares(this.verifiersPublicKeys.keySet());
        computePolynomialCoefficientCommitment();
        computeSharesCommitment();
        computePublicKeysCommitment();
        computeVerifiesInformation();
    }

    private void constructRandomPolynomial(int order) {
        log.info("Construct random polynomial");
        this.polynomial = new Polynomial(order, this.getZq(), this.s);
    }

    /**
     * 计算多项式份额
     */
    private void computeShares(Set<Integer> ids) {
        log.info("Polynomial shares are calculated for each IDVerify");
        this.shares = this.polynomial.computeShares(ids);
    }

    /**
     * 计算系数的承诺
     */
    private void computePolynomialCoefficientCommitment() {
        log.info("Calculate the polynomial coefficients commitment");
        Map<Integer, byte[]> coefficientsCommitment = this.polynomial.computePolynomialCoefficientsCommitment(this.generatorTwo);
        this.udAllVerifyInformation.setPolynomialCoefficientCommitment(coefficientsCommitment);
    }

    /**
     * 计算份额的承诺
     */
    private void computeSharesCommitment() {
        log.info("Calculate the shares commitment");
        Map<Integer, byte[]> sharesCommitment = this.polynomial.computeSharesCommitment(this.shares, this.generatorTwo);
        this.udAllVerifyInformation.setSharesCommitment(sharesCommitment);
    }

    /**
     * 计算份额对于公钥的承诺
     */
    private void computePublicKeysCommitment() {
        log.info("Calculate the publicKeys commitment");
        Map<Integer, byte[]> publicKeysCommitment = this.polynomial.computePublicKeysCommitment(this.verifiersPublicKeys, this.shares);
        this.udAllVerifyInformation.setPublicKeysCommitment(publicKeysCommitment);
    }

    /**
     * 计算双线性对的验证信息
     */
    private void computeVerifiesInformation() {
        log.info("Calculate the verifies information");
        Map<Integer, byte[]> verifiesInformation = this.polynomial.computeVerifiesInformation(this.udAllVerifyInformation.getSharesCommitment(), this.verifiersPublicKeys, this.pairing, this.G1);
        this.udAllVerifyInformation.setVerifyInformation(verifiesInformation);
    }


    /**
     * 计算身份信息
     */
    private void computeIdentityInformation() {
        log.info("Construct real identity information");
        this.identityInformation = constructIdentityInformation(this.name, this.id, this.email, this.phone);
    }

    /**
     * 计算身份保护信息
     */
    private void computeIdentityProtectionInformation() {
        log.info("Calculate identity protection information");
        this.identityProtectionInformation = ComputeUtils.xor(this.identityInformation, ComputeUtils.sha512(this.secret.toBytes()));
        this.udAllVerifyInformation.setIdentityProtectionInformation(this.identityProtectionInformation);
    }

    /**
     * 构造身份信息字节数组，字节数组长度为 secureParam * 4 / 8
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
        return identityInformation;
    }

    /**
     * 根据信息构造字节数组，长度限制为singleInformation，不足低位补0,高于后面截取
     */
    private byte[] constructSingleInformation(String singleInformation, int singleInformationLength) {
        byte[] singleInformationBytes = singleInformation.getBytes();
        return Arrays.copyOf(singleInformationBytes, singleInformationLength);
    }


    @Override
    protected void autowireSelfParamsToProperties(Properties properties) {
        properties.setProperty(Constant.SECRET, Base64.encodeBase64String(this.secret.toBytes()));
        properties.setProperty(Constant.S, Base64.encodeBase64String(this.s.toBytes()));
        properties.setProperty(Constant.PHONE, this.phone);
        properties.setProperty(Constant.EMAIL, this.email);
        properties.setProperty(Constant.NAME, this.name);
        properties.setProperty(Constant.ID, String.valueOf(this.id));
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
        this.setGeneratorOne(this.G1.newElementFromBytes(
                Base64.decodeBase64(publicParams.getGeneratorOneBase64())
        ).getImmutable());
        this.setGeneratorTwo(this.G1.newElementFromBytes(
                Base64.decodeBase64(publicParams.getGeneratorTwoBase64())
        ).getImmutable());
        //初始化秘密
        this.s = this.Zq.newRandomElement().getImmutable();
        this.secret = this.generatorOne.powZn(this.s).getImmutable();
        //初始化身份信息
        this.name = "zhangsan";
        this.id = 2343;
        this.email = "12321.@qq.com";
        this.phone = "123324788345";
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
