package com.multi.domain.iot.param;

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
    public static final String PARAMS_SAVE_PATH = "auditAgentParams.properties";

    /**
     * 单例模型获取参数
     * @param publicParams
     * @return
     */
    public static AuditAgentParams getInstance(PublicParams publicParams){
        if (auditAgentParams == null){
            synchronized (AuditAgentParamsFactory.class){
                if (auditAgentParams == null){
                    auditAgentParams = AuditAgentParams.build(publicParams,PARAMS_SAVE_PATH);
                }
            }
        }
        return auditAgentParams;
    }
}
