package com.lw.dillon.admin.framework.datapermission.core.util;

import com.lw.dillon.admin.framework.datapermission.core.aop.DataPermissionContextHolder;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;

public class DataPermissionUtilsTest {

    @Test
    public void testExecuteIgnore() {
        DataPermissionUtils.executeIgnore(() -> assertFalse(DataPermissionContextHolder.get().enable()));
    }

}
