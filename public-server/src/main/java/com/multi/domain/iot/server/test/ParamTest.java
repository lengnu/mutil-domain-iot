package com.multi.domain.iot.server.test;

import com.multi.domain.iot.common.param.PublicParams;

/**
 * @BelongsProject: Multi-Domain-IoT
 * @BelongsPackage: com.multi.domain.iot.test
 * @Author: duwei
 * @Date: 2022/11/17 15:58
 * @Description: TODO
 */
public class ParamTest {
    public static void main(String[] args) {
        PublicParams publicParams = new PublicParams();
        publicParams.initialize("a.properties","ll.properties");
        System.out.println(publicParams);

    }
}
