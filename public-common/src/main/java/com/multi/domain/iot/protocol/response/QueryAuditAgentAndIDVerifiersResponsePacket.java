package com.multi.domain.iot.protocol.response;

import com.multi.domain.iot.param.EnrollInformationOnPublicServer;
import com.multi.domain.iot.protocol.Packet;
import com.multi.domain.iot.protocol.message.MessageType;
import com.multi.domain.iot.role.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * @author duwei
 * @version 1.0.0
 * @create 2022-11-20 21:42
 * @description 查询AA和所有IDV的信息响应
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class QueryAuditAgentAndIDVerifiersResponsePacket extends Packet {
    private Map<Role, Map<Integer, EnrollInformationOnPublicServer>> roleInformationMap;
    private boolean success;

    @Override
    public byte getMessageType() {
        return MessageType.QUERY_AUDIT_AGENT_AND_ID_VERIFIERS_RESPONSE;
    }
}
