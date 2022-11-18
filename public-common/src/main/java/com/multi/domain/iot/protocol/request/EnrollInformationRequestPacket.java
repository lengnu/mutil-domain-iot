package com.multi.domain.iot.protocol.request;

import com.multi.domain.iot.protocol.Packet;
import com.multi.domain.iot.role.Role;
import lombok.Data;

/**
 * @BelongsProject: Multi-Domain-IoT
 * @BelongsPackage: com.multi.domain.iot.protocol.request
 * @Author: duwei
 * @Date: 2022/11/18 15:37
 * @Description: 注册信息到服务器，为了别的节点可以找到他
 */
@Data
public class EnrollInformationRequestPacket extends Packet {
    private String host;
    private int listenPort;
    private byte[] publicKey;
    private Role role;

    @Override
    public byte getMessageType() {
        return 0;
    }
}
