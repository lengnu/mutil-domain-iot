package com.multi.domain.iot.ud.starter;

import com.multi.domain.iot.common.domain.Domain;
import com.multi.domain.iot.ud.param.UDParamsFactory;

/**
 * @author duwei
 * @version 1.0.0
 * @create 2022-11-21 20:06
 * @description UD启动器
 */
public class UDStarter {
    public static void main(String[] args) throws InterruptedException {
        if (args.length != 2) {
            throw new RuntimeException("The input parameter is malformed");
        }
        int listenPort = Integer.parseInt(args[0]);
        Domain domain = Domain.valueOf(args[1]);
        UDParamsFactory.listenPort = listenPort;
        UDParamsFactory.domain = domain;
        UDServerStarter serverStarter = new UDServerStarter(listenPort,domain);
        UDClientStarter udClientStarter = new UDClientStarter();
    }
}
