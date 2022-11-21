package com.multi.domain.iot.common.param;

import com.multi.domain.iot.common.role.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @author duwei
 * @version 1.0.0
 * @create 2022-11-20 21:47
 * @description 各个实体注册在服务器上的信息
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class EnrollInformationOnPublicServer {
    private Integer id;
    private Role role;
    private byte[] publicKey;
    private String host;
    private int listenPort;
}
