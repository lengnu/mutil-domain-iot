package com.multi.domain.iot.verifier.validator;

import com.multi.domain.iot.common.message.UDAuthenticationMessage;
import com.multi.domain.iot.common.util.ComputeUtils;
import com.multi.domain.iot.common.validator.Validator;
import com.multi.domain.iot.verifier.param.IDVerifierParamsFactory;
import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Field;
import it.unisa.dia.gas.jpbc.Pairing;
import lombok.extern.slf4j.Slf4j;

import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @BelongsProject: Multi-Domain-IoT
 * @BelongsPackage: com.multi.domain.iot.verifier.validator
 * @Author: duwei
 * @Date: 2022/11/22 9:07
 * @Description: UD认证信息验证器
 */
@Slf4j
public class UDAuthenticationMessageValidator implements Validator {
    /**
     * UD传过来的认证信息
     */
    private UDAuthenticationMessage udAuthenticationMessage;

    public UDAuthenticationMessageValidator(UDAuthenticationMessage udAuthenticationMessage) {
        this.udAuthenticationMessage = udAuthenticationMessage;
    }

    @Override
    public boolean verify() {
        return verifySharesCorrectness() && verifySharesConsistency();
    }

    /**
     * 验证份额的正确性，即份额是按照构造的多项式生成
     */
    private boolean verifySharesCorrectness() {
        log.info("Start verifying share correctness");
        Map<Integer, byte[]> polynomialCoefficientsCommitment = udAuthenticationMessage.getPolynomialCoefficientsCommitment();
        Map<Integer, byte[]> sharesCommitment = udAuthenticationMessage.getSharesCommitment();
        for (Map.Entry<Integer, byte[]> shareCommitment : sharesCommitment.entrySet()) {
            if (!verifyShareCorrectness(polynomialCoefficientsCommitment, shareCommitment.getKey(), shareCommitment.getValue())) {
                log.error("Correctness validation failed");
                return false;
            }
        }
        log.info("Correctness validation passed");
        return true;
    }

    /**
     * 验证份额一致性
     */
    private boolean verifySharesConsistency() {
        log.info("Start verifying share consistency");
        boolean success = this.verifySharesConsistency(this.udAuthenticationMessage.getPublicKeySharesProtection(),
                this.udAuthenticationMessage.getVerifyInformation());
        if (success) {
            log.info("Consistency validation passed");
        } else {
            log.error("Consistency validation failed");
        }
        return success;
    }

    private boolean verifySharesConsistency(Map<Integer, byte[]> publicKeySharesProtection,
                                            Map<Integer, byte[]> verifyInformation
    ) {
        Pairing pairing = IDVerifierParamsFactory.getInstance().getPairing();
        Field G = pairing.getG1();
        Field GT = pairing.getGT();
        Element h = IDVerifierParamsFactory.getInstance().getGeneratorTwo();
        return this.verifySharesConsistency(publicKeySharesProtection, verifyInformation,
                pairing, G, GT, h);
    }

    /**
     * 批量验证份额的一致性，即UD生成份额的密文确实是由经过承诺的份额生成
     */
    private boolean verifySharesConsistency(Map<Integer, byte[]> publicKeySharesProtection,
                                            Map<Integer, byte[]> verifyInformation,
                                            Pairing pairing,
                                            Field G,
                                            Field GT,
                                            Element h) {
        Element left = ComputeUtils.calculateMultiplication(verifyInformation.values(), GT);
        Element right = pairing.pairing(h, ComputeUtils.calculateAccumulation(publicKeySharesProtection.values(), G));
        return left.equals(right);
    }




    private boolean verifyShareCorrectness(Map<Integer, byte[]> polynomialCoefficientsCommitment, int i,
                                           byte[] shareCommitment) {
        return this.verifyShareCorrectness(polynomialCoefficientsCommitment, i, IDVerifierParamsFactory.getInstance().getZq(), shareCommitment, IDVerifierParamsFactory.getInstance().getG1());
    }

    private boolean verifyShareCorrectness(Map<Integer, byte[]> polynomialCoefficientsCommitment, int i, Field Zq,
                                           byte[] shareCommitment, Field G) {
        Element one = G.newOneElement();
        Element iElement = Zq.newElement(i).getImmutable();
        //TODO 指数、可优化 -> 倍乘
        polynomialCoefficientsCommitment.forEach((coefficientIndex, coefficientCommitment) -> {
            Element coefficientIndexElement = Zq.newElement(coefficientIndex).getImmutable();
            Element coefficientCommitmentElement = G.newElementFromBytes(coefficientCommitment).getImmutable();
            Element exponential = iElement.powZn(coefficientIndexElement).getImmutable();
            Element cur = coefficientCommitmentElement.powZn(exponential).getImmutable();
            one.mul(cur);
        });
        Element shareCommitmentElement = G.newElementFromBytes(shareCommitment).getImmutable();
        return shareCommitmentElement.equals(one);
    }

}
