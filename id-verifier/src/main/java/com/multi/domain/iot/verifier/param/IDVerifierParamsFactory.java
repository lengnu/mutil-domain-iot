package com.multi.domain.iot.verifier.param;

import com.multi.domain.iot.common.domain.Domain;
import com.multi.domain.iot.common.param.PublicParams;
import com.multi.domain.iot.common.util.IPUtils;

/**
 * @author duwei
 * @version 1.0.0
 * @create 2022-11-20 19:16
 * @description 验证者的参数工厂
 */
public class IDVerifierParamsFactory {
    private static volatile IDVerifierParams idVerifierParams;
    public static final String PUBLIC_PARAMS_SAVE_PATH = "id-Verifier-public.properties";
    public static final String SELF_PARAMS_SAVE_PATH = "id-Verifier-self.properties";
    public static final String HOST = IPUtils.getLocalIp();
    public static int listenPort;
    public static Domain domain;

    /**
     * 单例模型获取参数
     * @param publicParams
     * @return
     */
    public static IDVerifierParams getInstance(PublicParams publicParams){
        if (idVerifierParams == null){
            synchronized (IDVerifierParams.class){
                if (idVerifierParams == null){
                    idVerifierParams =new IDVerifierParams(publicParams,PUBLIC_PARAMS_SAVE_PATH,SELF_PARAMS_SAVE_PATH);
                    idVerifierParams.setHost(HOST);
                    idVerifierParams.setDomain(domain);
                    idVerifierParams.setListenPort(listenPort);
                }
            }
        }
        return idVerifierParams;
    }



    public static IDVerifierParams getInstance(){
        if (idVerifierParams == null){
            throw new RuntimeException("The idVerifierParams has not been initialized");
        }
        return idVerifierParams;
    }
}
