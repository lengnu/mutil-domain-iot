package com.multi.domain.iot.auditagent.starter;

import com.multi.domain.iot.auditagent.param.AuditAgentParamsFactory;


/**
 * @BelongsProject: Multi-Domain-IoT
 * @BelongsPackage: com.multi.domain.iot.auditagent.starter
 * @Author: duwei
 * @Date: 2022/11/21 15:40
 * @Description: 审计代理启动类
 */
public class AuditAgentStarter {
    public static void main(String[] args) throws InterruptedException {
        if (args.length != 1) {
            throw new RuntimeException("The input parameter is malformed");
        }
        int listenPort = Integer.parseInt(args[0]);
        AuditAgentParamsFactory.listenPort = listenPort;
        AuditAgentServerStarter serverStarter = new AuditAgentServerStarter(listenPort);
        AuditAgentClientStarter clientStarter = new AuditAgentClientStarter();
    }
}
