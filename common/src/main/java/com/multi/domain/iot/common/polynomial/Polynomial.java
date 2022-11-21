package com.multi.domain.iot.common.polynomial;

import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Field;
import it.unisa.dia.gas.jpbc.Pairing;
import it.unisa.dia.gas.plaf.jpbc.field.z.ZElement;
import it.unisa.dia.gas.plaf.jpbc.field.z.ZrElement;
import it.unisa.dia.gas.plaf.jpbc.field.z.ZrField;
import it.unisa.dia.gas.plaf.jpbc.pairing.PairingFactory;
import it.unisa.dia.gas.plaf.jpbc.pairing.a.TypeACurveGenerator;
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
    private final Field G;
    /**
     * 多项式的系数
     */
    private final Element[] coefficients;

    public Polynomial(int order, Field zq, Field G,Element s) {
        this.order = order;
        this.Zq = zq;
        this.G = G;
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
            Element commitment = computePolynomialCoefficientCommitment(h,this.coefficients[i]);
            result.put(i, commitment.toBytes());
        }
        return result;
    }

    private Element computePolynomialCoefficientCommitment(Element h,Element coefficient){
        return h.powZn(coefficient);
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
        Element zrShare = this.Zq.newElementFromBytes(share).getImmutable();
        return h.powZn(zrShare);
    }


    /**
     * 生成计算保护Yi
     */
    public  Map<Integer, byte[]> computePublicKeySharesProtection(Map<Integer, byte[]> publicKeys, Map<Integer, byte[]> shares) {
        Map<Integer, byte[]> result = new HashMap<>();
        publicKeys.forEach((id, publicKey) -> {
            byte[] share = shares.get(id);
            Element commitment =computePublicKeyShareProtection(publicKey,share);
            result.put(id, commitment.toBytes());
        });
        return result;
    }

    private  Element computePublicKeyShareProtection(byte[] publicKey,byte[] share){
        Element publicKeyElement = this.G.newElementFromBytes(publicKey).getImmutable();
        Element shareElement = this.Zq.newElementFromBytes(share).getImmutable();
        return publicKeyElement.powZn(shareElement);
    }

    public static void main(String[] args) {
        TypeACurveGenerator typeACurveGenerator = new TypeACurveGenerator(4, 4);
        Pairing pairing = PairingFactory.getPairing(typeACurveGenerator.generate());
        Field zr = pairing.getZr();
        System.out.println(zr);
        System.out.println(zr.getOrder());
        Element secret = zr.newElement(3);
        Polynomial polynomial = new Polynomial(2, zr, zr,secret);
        System.out.println("polynomial = " + Arrays.toString(polynomial.coefficients));
        Map<Integer, byte[]> shares = polynomial.computeShares(new HashSet<>(Arrays.asList(1, 2, 3)));
        shares.forEach((x,y) -> {
            System.out.println("x = " + x +  "\t y = " + zr.newElementFromBytes(y));
        });

        Element h = zr.newElement(2).getImmutable();
        Map<Integer, byte[]> integerMap1 = polynomial.computePolynomialCoefficientsCommitment(h);

        integerMap1.forEach((index,commit) -> {
            System.out.println("a_" + index + "\t  commit " + zr.newElementFromBytes(commit));
        });

        polynomial.computeSharesCommitment(shares,h).forEach((id,comm) -> {
            System.out.println("id = " + id + " share commit = " + zr.newElementFromBytes(comm));
        });

        System.out.println("polynomial = " + Arrays.toString(polynomial.coefficients));

    }

}
