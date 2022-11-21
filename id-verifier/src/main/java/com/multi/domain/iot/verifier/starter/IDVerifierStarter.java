package com.multi.domain.iot.verifier.starter;

import com.multi.domain.iot.common.domain.Domain;
import com.multi.domain.iot.verifier.param.IDVerifierParamsFactory;

/**
 * @BelongsProject: Multi-Domain-IoT
 * @BelongsPackage: com.multi.domain.iot.verifier.starter
 * @Author: duwei
 * @Date: 2022/11/21 14:14
 * @Description: ID-Verifier启动类
 */
public class IDVerifierStarter {
    public static void main(String[] args) throws InterruptedException {
        if (args.length != 2) {
            throw new RuntimeException("The input parameter is malformed");
        }
        int listenPort = Integer.parseInt(args[0]);
        IDVerifierParamsFactory.listenPort = listenPort;
        Domain domain = Domain.valueOf(args[1]);
        IDVerifierParamsFactory.domain = domain;
        IDVerifierServerStarter serverStarter = new IDVerifierServerStarter(listenPort, domain);
        IDVerifierClientStarter clientStarter = new IDVerifierClientStarter();
    }
}
