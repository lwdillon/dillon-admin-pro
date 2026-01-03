package com.dillon.lw.module.system.job;

import com.dillon.lw.framework.quartz.core.handler.JobHandler;
import com.dillon.lw.framework.tenant.core.context.TenantContextHolder;
import com.dillon.lw.module.system.dal.dataobject.user.AdminUserDO;
import com.dillon.lw.module.system.dal.mysql.user.AdminUserMapper;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

@Component
public class DemoJob implements JobHandler {

    @Resource
    private AdminUserMapper adminUserMapper;

    @Override
    public String execute(String param) {
        System.out.println("当前租户：" + TenantContextHolder.getTenantId());
        List<AdminUserDO> users = adminUserMapper.selectList();
        return "用户数量：" + users.size();
    }

}
