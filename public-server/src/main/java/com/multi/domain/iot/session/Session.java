package com.multi.domain.iot.session;

import com.multi.domain.iot.role.Role;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @BelongsProject: Multi-Domain-IoT
 * @BelongsPackage: com.multi.domain.iot.session
 * @Author: duwei
 * @Date: 2022/11/18 15:23
 * @Description: 保存用户信息
 */
@Data
@AllArgsConstructor
public class Session {
    private Integer id;
    private Role role;
    private byte[] publicKey;
    private String ip;
    private int listenPort;
}
