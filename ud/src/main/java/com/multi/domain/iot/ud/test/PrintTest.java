package com.multi.domain.iot.ud.test;

import com.multi.domain.iot.common.message.UDAuthenticationMessage;
import org.apache.commons.codec.binary.Base64;

import java.util.Map;

/**
 * @BelongsProject: Multi-Domain-IoT
 * @BelongsPackage: com.multi.domain.iot.ud.test
 * @Author: duwei
 * @Date: 2022/11/22 10:23
 * @Description: TODO
 */
public class PrintTest {
    public static void printUDAuthenticationMessage(UDAuthenticationMessage udAuthenticationMessage){
        Map<Integer, byte[]> sharesCommitment = udAuthenticationMessage.getSharesCommitment();
        System.out.println("----------sharesCommitment-----------");
        for (Map.Entry<Integer,byte[]> shareCommitment : sharesCommitment.entrySet()){
            System.out.println("份额系数:" + shareCommitment.getKey() + "\t份额承诺:" + Base64.encodeBase64String(shareCommitment.getValue()));
        }
        System.out.println();


        Map<Integer, byte[]> polynomialCoefficientsCommitment = udAuthenticationMessage.getPolynomialCoefficientsCommitment();
        System.out.println("----------polynomialCoefficientsCommitment-----------");
        for (Map.Entry<Integer,byte[]> polynomialCoefficientCommitment : polynomialCoefficientsCommitment.entrySet()){
            System.out.println("多项式系数:" + polynomialCoefficientCommitment.getKey() + "\t多项式系数承诺:" + Base64.encodeBase64String(polynomialCoefficientCommitment.getValue()));
        }
        System.out.println();
    }
}
