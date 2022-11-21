package com.multi.domain.iot.common.domain;

/**
 * @BelongsProject: Multi-Domain-IoT
 * @BelongsPackage: com.multi.domain.iot.common.domain
 * @Author: duwei
 * @Date: 2022/11/21 15:29
 * @Description: 节点所处的域
 */
public enum Domain {
    /**
     * A域  ~ E域
     */
    A("A"),B("B"),C("C"),D("D"),E("E");
    private String domain;

    Domain(String domain) {
        this.domain = domain;
    }

    public String getDomain() {
        return domain;
    }

}
