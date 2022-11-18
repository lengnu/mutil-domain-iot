package com.multi.domain.iot.role;

/**
 * @BelongsProject: Multi-Domain-IoT
 * @BelongsPackage: com.multi.domain.iot.role
 * @Author: duwei
 * @Date: 2022/11/18 15:25
 * @Description: 用户角色
 */
public enum Role {
    /**
     * 三种角色
     */
    AA("AA"),UD("UD"),VV("VV");

    private final String role;

    Role(String role) {
        this.role = role;
    }

    public String getRole() {
        return role;
    }
}
