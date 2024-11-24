package com.lw.swing.request.loadbalancer;

import com.netflix.client.config.IClientConfig;
import com.netflix.loadbalancer.AbstractLoadBalancerRule;
import com.netflix.loadbalancer.Server;

import java.util.List;

public class PrimaryBackupRule extends AbstractLoadBalancerRule {
    private int primaryIndex = 0; // 定义第一个服务为优先

    @Override
    public Server choose(Object key) {
        List<Server> servers = getLoadBalancer().getAllServers();
        if (servers.isEmpty()) {
            return null;
        }

        Server primaryServer = servers.get(primaryIndex);
        if (primaryServer.isAlive()) {
            return primaryServer; // 优先返回第一个服务
        } else {
            // 主机不可用时，选择其他可用服务
            return servers.stream().filter(Server::isAlive).skip(1).findFirst().orElse(null);
        }
    }

    @Override
    public void initWithNiwsConfig(IClientConfig clientConfig) {
        // 可以配置初始化行为
    }
}