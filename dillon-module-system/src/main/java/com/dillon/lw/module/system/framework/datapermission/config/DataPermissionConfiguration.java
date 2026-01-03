package com.dillon.lw.module.system.framework.datapermission.config;

import com.dillon.lw.framework.datapermission.core.rule.dept.DeptDataPermissionRuleCustomizer;
import com.dillon.lw.module.system.dal.dataobject.dept.DeptDO;
import com.dillon.lw.module.system.dal.dataobject.user.AdminUserDO;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * system 模块的数据权限 Configuration
 *
 * @author liwen
 */
@Configuration(proxyBeanMethods = false)
public class DataPermissionConfiguration {

    @Bean
    public DeptDataPermissionRuleCustomizer sysDeptDataPermissionRuleCustomizer() {
        return rule -> {
            // dept
            rule.addDeptColumn(AdminUserDO.class);
            rule.addDeptColumn(DeptDO.class, "id");
            // user
            rule.addUserColumn(AdminUserDO.class, "id");
        };
    }

}
