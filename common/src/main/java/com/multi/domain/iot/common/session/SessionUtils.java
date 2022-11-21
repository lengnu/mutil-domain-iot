package com.multi.domain.iot.common.session;

import com.multi.domain.iot.common.domain.Domain;
import com.multi.domain.iot.common.protocol.request.EnrollInformationRequestPacket;
import com.multi.domain.iot.common.role.Role;


import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @BelongsProject: Multi-Domain-IoT
 * @BelongsPackage: com.multi.domain.iot.common.session
 * @Author: duwei
 * @Date: 2022/11/21 16:59
 * @Description: 会话工具类
 */
public class SessionUtils {
    /**
     * 为注册的用户分配ID
     */
    private static final AtomicInteger ATOMIC_INTEGER = new AtomicInteger(1);
    private static Session auditAgentSession;
    private static final Map<Domain,Map<Integer,Session>> DOMAIN_ID_VERIFIERS_SESSIONS = new ConcurrentHashMap<>();


    public static Session getAuditAgentSession() {
        return auditAgentSession;
    }

    public static Map<Integer, Session> getIdVerifiersSessions(Domain domain) {
        return DOMAIN_ID_VERIFIERS_SESSIONS.get(domain);
    }

    /**
     * -1   失败
     * > 0  分配给对应用户的id
     */
    public static int bindSession(EnrollInformationRequestPacket requestPacket) {
        return bindSession(requestPacket.getRole(),
                requestPacket.getHost(),
                requestPacket.getListenPort(),
                requestPacket.getPublicKey(),
                requestPacket.getDomain());
    }

    private static int bindSession(Role role, String host, int listenPort, byte[] publicKey, Domain domain) {
        if (role == Role.AA) {
            return bindAuditAgentSession(role, host, listenPort, publicKey);
        } else if (role == Role.IDV) {
            return bindIDVerifierSession(role, host, listenPort, publicKey, domain);
        }
        return -1;
    }

    /**
     * 将auditAgent信息保存
     */
    private synchronized static int bindAuditAgentSession(Role role, String host, int listenPort, byte[] publicKey) {
        if (auditAgentSession != null) {
            return -1;
        }
        int id = ATOMIC_INTEGER.getAndIncrement();
        Session session = new Session(id, publicKey, role, host, listenPort, null);
        auditAgentSession = session;
        return id;
    }

    private static int bindIDVerifierSession(Role role, String host, int listenPort, byte[] publicKey, Domain domain) {
        int id = ATOMIC_INTEGER.getAndIncrement();
        Session session = new Session(id, publicKey, role, host, listenPort, domain);
        if (DOMAIN_ID_VERIFIERS_SESSIONS.get(domain) == null){
            Map<Integer,Session> idSessionMap = new HashMap<>();
            DOMAIN_ID_VERIFIERS_SESSIONS.put(domain,idSessionMap);
        }
        DOMAIN_ID_VERIFIERS_SESSIONS.get(domain).put(id,session);
        return id;
    }
}
