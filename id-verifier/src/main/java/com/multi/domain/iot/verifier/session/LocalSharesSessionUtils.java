package com.multi.domain.iot.verifier.session;


import com.multi.domain.iot.common.message.UDAuthenticationMessage;
import com.multi.domain.iot.verifier.param.IDVerifierParamsFactory;
import org.apache.commons.codec.binary.Base64;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @BelongsProject: Multi-Domain-IoT
 * @BelongsPackage: com.multi.domain.iot.verifier.session
 * @Author: duwei
 * @Date: 2022/11/22 15:04
 * @Description: 记录UD的身份信息和对应的份额
 */
public class LocalSharesSessionUtils {
    //身份保护信息位Key(经过Base64编码的)
    private static final Map<String,byte[]> UD_SHARES_MAP = new ConcurrentHashMap<>();

    private static void bindSession(byte[] udIdentityProtectionInformation,byte[] publicKeyShareProtection){
        UD_SHARES_MAP.put(Base64.encodeBase64String(udIdentityProtectionInformation),publicKeyShareProtection);
    }

    public static void bindSession(UDAuthenticationMessage udAuthenticationMessage){
        bindSession(udAuthenticationMessage.getIdentityProtectionInformation(),
                udAuthenticationMessage.getPublicKeySharesProtection().get(IDVerifierParamsFactory.getInstance().getId()));
    }
}
