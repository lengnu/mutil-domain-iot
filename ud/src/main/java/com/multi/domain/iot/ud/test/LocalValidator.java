package com.multi.domain.iot.ud.test;

import com.multi.domain.iot.common.message.UDAuthenticationMessage;
import com.multi.domain.iot.common.validator.Validator;
import com.multi.domain.iot.ud.param.UDParamsFactory;
import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Field;

import java.util.Map;

/**
 * @BelongsProject: Multi-Domain-IoT
 * @BelongsPackage: com.multi.domain.iot.ud.test
 * @Author: duwei
 * @Date: 2022/11/22 10:02
 * @Description: TODO
 */
public class LocalValidator implements Validator {
    /**
     * UD传过来的认证信息
     */
    private UDAuthenticationMessage udAuthenticationMessage;

    public LocalValidator(UDAuthenticationMessage udAuthenticationMessage) {
        this.udAuthenticationMessage = udAuthenticationMessage;
    }

    @Override
    public boolean verify() {
        return verifyShares();
    }


    private boolean verifyShares() {
        Map<Integer, byte[]> polynomialCoefficientsCommitment = udAuthenticationMessage.getPolynomialCoefficientsCommitment();
        Map<Integer, byte[]> sharesCommitment = udAuthenticationMessage.getSharesCommitment();
//        for (Map.Entry<Integer,byte[]> shareCommitment: sharesCommitment.entrySet()){
//            if (!verifyShare(polynomialCoefficientsCommitment,shareCommitment.getKey(),shareCommitment.getValue())){
//                return false;
//            }
//        }
//        return true;
        boolean success = true;
        for (Map.Entry<Integer, byte[]> shareCommitment : sharesCommitment.entrySet()) {
            if (!verifyShare(polynomialCoefficientsCommitment, shareCommitment.getKey(), shareCommitment.getValue())) {
                success = false;
            }
        }
        return success;

    }


    private boolean verifyShare(Map<Integer, byte[]> polynomialCoefficientsCommitment, int i, byte[] shareCommitment) {
        return this.verifyShare(polynomialCoefficientsCommitment, i,
                UDParamsFactory.getInstance().getZq(), shareCommitment,UDParamsFactory.getInstance().getG1());
    }

    private boolean verifyShare(Map<Integer, byte[]> polynomialCoefficientsCommitment, int i, Field Zq, byte[] shareCommitment,Field G) {
        Element one = G.newOneElement();
        Element iElement = Zq.newElement(i).getImmutable();
        //TODO 指数、可优化 -> 倍乘
        polynomialCoefficientsCommitment.forEach((coefficientIndex, coefficientCommitment) -> {
            Element coefficientIndexElement = Zq.newElement(coefficientIndex).getImmutable(); //K
            Element coefficientCommitmentElement = G.newElementFromBytes(coefficientCommitment).getImmutable(); //C_K
            Element exponential = iElement.powZn(coefficientIndexElement).getImmutable();
            Element cur = coefficientCommitmentElement.powZn(exponential).getImmutable();
            one.mul(cur);
        });
        Element shareCommitmentElement = G.newElementFromBytes(shareCommitment).getImmutable();
        boolean sucess = shareCommitmentElement.equals(one);
        System.out.println("i = " + i + " \t success = " + sucess);
        return sucess;
    }

}
