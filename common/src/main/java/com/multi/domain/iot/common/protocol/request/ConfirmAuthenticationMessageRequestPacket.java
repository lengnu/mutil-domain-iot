package com.multi.domain.iot.common.protocol.request;

import com.multi.domain.iot.common.domain.Domain;
import com.multi.domain.iot.common.protocol.Packet;
import com.multi.domain.iot.common.protocol.message.MessageType;
import lombok.Data;

import java.net.InetSocketAddress;

/**
 * @BelongsProject: Multi-Domain-IoT
 * @BelongsPackage: com.multi.domain.iot.common.protocol.request
 * @Author: duwei
 * @Date: 2022/11/22 14:59
 * @Description: 确认消息请求数据包
 */
@Data
public class ConfirmAuthenticationMessageRequestPacket extends Packet {
    private boolean success;
    private Integer id;
    //确认消息
    private byte[] confirmInformation;
    private byte[] identityProtectionInformation;
    private int totalVerifiersNumber;
    //是哪个UD的消息
    private InetSocketAddress udAddress;
    private Domain domain;
    @Override
    public byte getMessageType() {
        return MessageType.CONFIRM_AUTHENTICATION_MESSAGE_REQUEST;
    }
}
