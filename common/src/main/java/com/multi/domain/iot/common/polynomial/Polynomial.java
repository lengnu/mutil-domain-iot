package com.multi.domain.iot.common.polynomial;

import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Field;
import it.unisa.dia.gas.jpbc.Pairing;
import lombok.Data;

import java.util.*;

/**
 * @BelongsProject: Multi-Domain-IoT
 * @BelongsPackage: com.multi.domain.iot.polynomial
 * @Author: duwei
 * @Date: 2022/11/21 9:26
 * @Description: 多项式实体
 */
@Data
public class Polynomial {
    /**
     * 多项式的阶
     */
    private final int order;
    /**
     * 多项式的运算域
     */
    private final Field Zq;
    /**
     * 多项式的系数
     */
    private final Element[] coefficients;

    public Polynomial(int order, Field zq, Element s) {
        this.order = order;
        this.Zq = zq;
        this.coefficients = new Element[order + 1];
        this.init(s);
    }

    //初始化多项式系数
    private void init(Element s) {
        this.coefficients[0] = s.getImmutable();
        for (int i = 1; i <= this.order; i++) {
            this.coefficients[i] = this.Zq.newRandomElement().getImmutable();
        }
    }

    public Map<Integer, byte[]> computeShares(Set<Integer> ids) {
        Map<Integer, byte[]> result = new HashMap<>();
        for (Integer id : ids) {
            Element share = computeShare(id);
            result.put(id, share.toBytes());
        }
        return result;
    }

    private Element computeShare(Integer id) {
        Objects.requireNonNull(this.coefficients, "多项式还未初始化");
        Element idElement = this.Zq.newElement(id).getImmutable();
        Element result = this.Zq.newZeroElement();
        Element base = this.Zq.newOneElement();

        for (int i = 0; i <= this.order; i++) {
            Element temp = this.coefficients[i].mul(base);
            base.mul(idElement);
            result.add(temp);
        }
        return result.getImmutable();
    }


    /**
     * 计算对多项系系数的承诺,key - 系数索引,value - 系数承诺
     */
    public Map<Integer, byte[]> computePolynomialCoefficientsCommitment(Element h) {
        Map<Integer, byte[]> result = new HashMap<>();
        for (int i = 0; i < this.coefficients.length; i++) {
            Element commitment = computePolynomialCoefficientsCommitment(h,this.coefficients[i]);
            result.put(i, commitment.toBytes());
        }
        return result;
    }

    private Element computePolynomialCoefficientsCommitment(Element h,Element coefficient){
        return h.powZn(coefficient).getImmutable();
    }


    /**
     * 为每个用户的份额计算承诺Xi
     */
    public  Map<Integer, byte[]> computeSharesCommitment(Map<Integer, byte[]> shares, Element h) {
        Map<Integer, byte[]> result = new HashMap<>();
        shares.forEach((id, share) -> {
            Element commitment = computeShareCommitment(share,h);
            result.put(id, commitment.toBytes());
        });
        return result;
    }


    private Element computeShareCommitment(byte[] share,Element h){
        Element zrShare = this.Zq.newElementFromBytes(share);
        return h.powZn(zrShare).getImmutable();
    }


    /**
     * 生成计算保护Yi
     */
    public  Map<Integer, byte[]> computePublicKeysCommitment(Map<Integer, byte[]> publicKeys, Map<Integer, byte[]> shares) {
        Map<Integer, byte[]> result = new HashMap<>();
        publicKeys.forEach((id, publicKey) -> {
            byte[] share = shares.get(id);
            Element commitment =computePublicKeyCommitment(publicKey,share);
            result.put(id, commitment.toBytes());
        });
        return result;
    }

    private  Element computePublicKeyCommitment(byte[] publicKey,byte[] share){
        Element publicKeyElement = this.Zq.newElementFromBytes(publicKey);
        Element shareElement = this.Zq.newElementFromBytes(share);
        return publicKeyElement.powZn(shareElement).getImmutable();
    }


    /**
     * 计算对应的验证信息
     */
    public  Map<Integer, byte[]> computeVerifiesInformation(Map<Integer, byte[]> sharesCommitment, Map<Integer, byte[]> publicKeys, Pairing pairing,Field G) {
        Map<Integer, byte[]> result = new HashMap<>();
        sharesCommitment.forEach((id, shareCommitment) -> {
            byte[] publicKey = publicKeys.get(id);
            Element verifyInformation = computeVerifyInformation(shareCommitment,publicKey,pairing,G);
            result.put(id, verifyInformation.toBytes());
        });
        return result;
    }

    private  Element computeVerifyInformation(byte[] shareCommitment,byte[] publicKey,Pairing pairing,Field G){
        Element shareCommitmentElement = G.newElementFromBytes(shareCommitment);
        Element publicKeyElement = G.newElementFromBytes(publicKey);
        return pairing.pairing(shareCommitmentElement,publicKeyElement).getImmutable();
    }


}
