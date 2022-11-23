package com.multi.domain.iot.common.message;

import com.multi.domain.iot.common.domain.Domain;
import lombok.Data;

import java.net.InetSocketAddress;
import java.util.Map;

/**
 * @BelongsProject: Multi-Domain-IoT
 * @BelongsPackage: com.multi.domain.iot.entity
 * @Author: duwei
 * @Date: 2022/11/21 10:34
 * @Description: UD需要广播的所有验证信息
 */
@Data
public class UDAuthenticationMessage {
    //真实身份保护信息 \tao
    private byte[] identityProtectionInformation;
    //多项式系数承诺C
    private Map<Integer,byte[]> polynomialCoefficientsCommitment;
    //对每个IDV的份额承诺X
    private Map<Integer,byte[]> sharesCommitment;
    //公钥对份额的承诺Y
    private Map<Integer,byte[]> publicKeySharesProtection;
    //使用份额承诺和公钥生成的验证信息
    private Map<Integer,byte[]> verifyInformation;
    private int totalVerifiersNumber;
    //消息的源，即ud的地址
    private InetSocketAddress udAddress;
    private Domain domain;
}
