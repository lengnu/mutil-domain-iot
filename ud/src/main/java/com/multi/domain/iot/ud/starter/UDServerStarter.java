package com.multi.domain.iot.ud.starter;

import com.multi.domain.iot.common.domain.Domain;
import lombok.extern.slf4j.Slf4j;

/**
 * @BelongsProject: Multi-Domain-IoT
 * @BelongsPackage: com.multi.domain.iot.ud.starter
 * @Author: duwei
 * @Date: 2022/11/21 17:25
 * @Description: UD作为服务器启动类
 */
@Slf4j
public class UDServerStarter {

    private final int listenPort;
    private final Domain domain;

    public UDServerStarter(int listenPort, Domain domain) {
        this.listenPort = listenPort;
        this.domain = domain;
    }
}
