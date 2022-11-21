package com.multi.domain.iot.common.entity;

import lombok.Data;

import java.util.Map;

/**
 * @BelongsProject: Multi-Domain-IoT
 * @BelongsPackage: com.multi.domain.iot.entity
 * @Author: duwei
 * @Date: 2022/11/21 10:34
 * @Description: UD需要广播的所有验证信息
 */
@Data
public class UDAllVerifyInformation {
    //真实身份保护信息 \tao
    private byte[] identityProtectionInformation;
    //多项式系数承诺C
    private Map<Integer,byte[]> polynomialCoefficientCommitment;
    //对每个IDV的份额承诺X
    private Map<Integer,byte[]> sharesCommitment;
    //公钥对份额的承诺Y
    private Map<Integer,byte[]> publicKeysCommitment;
    //使用份额承诺和公钥生成的验证信息
    private Map<Integer,byte[]> verifyInformation;
}
