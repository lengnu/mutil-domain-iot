package com.multi.domain.iot.session;

import com.multi.domain.iot.role.Role;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @BelongsProject: Multi-Domain-IoT
 * @BelongsPackage: com.multi.domain.iot.session
 * @Author: duwei
 * @Date: 2022/11/18 15:30
 * @Description: 保存全局注册信息
 */
public class SessionUtils {
    /**
     * 为注册的用户分配ID
     */
    private static AtomicInteger atomicInteger = new AtomicInteger(0);
    /**
     * 维护全局的节点信息
     */
    private static Map<Role, Map<Integer, Session>> informationMap = new ConcurrentHashMap<>();

    //返回用户的id信息
    public static int bindSession(Role role, String ip, int listenPort, byte[] publicKey) {
        Session session = new Session(atomicInteger.getAndIncrement(),role,publicKey,ip,listenPort);
        Map<Integer,Session> userInformation = new HashMap<>();
        userInformation.put(session.getId(),session);
        informationMap.put(role,userInformation);
        return session.getId();
    }
}
