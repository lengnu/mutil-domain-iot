package com.multi.domain.iot.auditagent.param;

import com.multi.domain.iot.common.domain.Domain;
import com.multi.domain.iot.common.param.PublicParams;
import com.multi.domain.iot.common.util.IPUtils;
import lombok.Data;

/**
 * @author duwei
 * @version 1.0.0
 * @create 2022-11-17 23:04
 * @description 参数工厂
 */
@Data
public class AuditAgentParamsFactory {
    private static volatile AuditAgentParams auditAgentParams;
    public static final String PUBLIC_PARAMS_SAVE_PATH = "auditAgentParams-public.properties";
    public static final String SELF_PARAMS_SAVE_PATH = "auditAgentParams-self.properties";
    public static final String HOST = IPUtils.getLocalIp();
    public static int listenPort;

    public static AuditAgentParams getInstance(){
        if (auditAgentParams == null){
            throw new RuntimeException("The auditAgentParams has not been initialized......");
        }
        return auditAgentParams;
    }

    /**
     * 单例模型获取参数
     * @param publicParams
     * @return
     */
    public static AuditAgentParams getInstance(PublicParams publicParams){
        if (auditAgentParams == null){
            synchronized (AuditAgentParamsFactory.class){
                if (auditAgentParams == null){
                    auditAgentParams = new AuditAgentParams(publicParams,PUBLIC_PARAMS_SAVE_PATH, SELF_PARAMS_SAVE_PATH);
                    auditAgentParams.setHost(HOST);
                    auditAgentParams.setListenPort(listenPort);
                }
            }
        }
        return auditAgentParams;
    }
}
