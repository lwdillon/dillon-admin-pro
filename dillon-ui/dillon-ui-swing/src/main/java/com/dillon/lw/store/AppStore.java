package com.dillon.lw.store;

import com.dillon.lw.SwingExceptionHandler;
import com.dillon.lw.api.system.DictDataApi;
import com.dillon.lw.module.system.controller.admin.auth.vo.AuthLoginRespVO;
import com.dillon.lw.module.system.controller.admin.auth.vo.AuthPermissionInfoRespVO;
import com.dillon.lw.module.system.controller.admin.dict.vo.data.DictDataSimpleRespVO;
import com.dillon.lw.subject.MenuRefrestObservable;
import com.dillon.lw.utils.DictTypeEnum;
import com.dtflys.forest.Forest;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Slf4j
public class AppStore {

    private static AuthLoginRespVO authLoginRespVO;
    private static AuthPermissionInfoRespVO authPermissionInfoRespVO;
    private static final MenuRefrestObservable menuRefreshObservable = new MenuRefrestObservable();

    private static Map<String, List<DictDataSimpleRespVO>> dictDataListMap;


    /**
     * 获取功能面板
     *
     * @param className
     */
    public static Container getNavigatonPanel(String className) {

        JComponent container = null;
        if (container == null) {
            Class<?> clazz = null;
            try {
                clazz = Class.forName(className);
                container = (JComponent) clazz.newInstance();
                container.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
                container.setOpaque(false);
            } catch (Exception e1) {
                container = new JLabel("暂无投运", JLabel.CENTER);
                log.error("获取功能面板出错:[" + className + "] as:" + e1);
                e1.printStackTrace();
            }
        }
        return container;
    }

    public static void setAuthLoginRespVO(AuthLoginRespVO authLoginRespVO) {
        AppStore.authLoginRespVO = authLoginRespVO;
    }

    public static String getAccessToken() {
        if (authLoginRespVO == null) {
            return "";
        }
        return authLoginRespVO.getAccessToken();
    }

    public static String getRefreshToken() {
        if (authLoginRespVO == null) {
            return "";
        }
        return authLoginRespVO.getRefreshToken();
    }

    public static LocalDateTime getExpiresTime() {
        if (authLoginRespVO == null) {
            return null;
        }
        return authLoginRespVO.getExpiresTime();
    }

    public static Long getUserId() {
        if (authLoginRespVO == null) {
            return null;
        }
        return authLoginRespVO.getUserId();
    }

    public static AuthLoginRespVO getAuthLoginRespVO() {
        return authLoginRespVO;
    }

    public static AuthPermissionInfoRespVO getAuthPermissionInfoRespVO() {
        return authPermissionInfoRespVO;
    }

    public static void setAuthPermissionInfoRespVO(AuthPermissionInfoRespVO authPermissionInfoRespVO) {
        AppStore.authPermissionInfoRespVO = authPermissionInfoRespVO;
    }


    /**
     * 用户信息
     *
     * @return {@link AuthPermissionInfoRespVO.UserVO }
     */
    public static AuthPermissionInfoRespVO.UserVO getUserVO() {
        if (authPermissionInfoRespVO == null) {
            return null;
        }
        return authPermissionInfoRespVO.getUser();
    }

    /**
     * 操作权限数组
     *
     * @return {@link Set }<{@link String }>
     */
    public static Set<String> getPermissions() {
        if (authPermissionInfoRespVO == null) {
            return null;
        }
        return authPermissionInfoRespVO.getPermissions();
    }


    /**
     * 角色标识数组
     *
     * @return {@link Set }<{@link String }>
     */
    public static Set<String> getRoles() {
        if (authPermissionInfoRespVO == null) {
            return null;
        }
        return authPermissionInfoRespVO.getRoles();
    }

    /**
     * 菜单树
     *
     * @return {@link Set }<{@link String }>
     */
    public static List<AuthPermissionInfoRespVO.MenuVO> getMenus() {
        if (authPermissionInfoRespVO == null) {
            return null;
        }
        return authPermissionInfoRespVO.getMenus();
    }

    public static Map<String, List<DictDataSimpleRespVO>> getDictDataListMap() {
        return dictDataListMap;
    }


    public static void setDictDataListMap(Map<String, List<DictDataSimpleRespVO>> dictDataListMap) {
        AppStore.dictDataListMap = dictDataListMap;
    }

    public static List<DictDataSimpleRespVO> getDictDataList(DictTypeEnum dictType) {
        return dictDataListMap.get(dictType.getType());
    }

    public static Map<String, DictDataSimpleRespVO> getDictDataValueMap(DictTypeEnum dictType) {
        List<DictDataSimpleRespVO> dictDataSimpleRespVOList = dictDataListMap.get(dictType.getType());

        // 将 List 转换为 Map，使用 id 作为键
        Map<String, DictDataSimpleRespVO> reultMap = dictDataSimpleRespVOList.stream()
                .collect(Collectors.toMap(DictDataSimpleRespVO::getValue, item -> item));

        return reultMap;
    }

    public static Map<String, DictDataSimpleRespVO> getDictDataMap(DictTypeEnum dictType) {
        List<DictDataSimpleRespVO> dictDataSimpleRespVOList = dictDataListMap.get(dictType.getType());

        // 将 List 转换为 Map，使用 id 作为键
        Map<String, DictDataSimpleRespVO> reultMap = dictDataSimpleRespVOList.stream()
                .collect(Collectors.toMap(DictDataSimpleRespVO::getLabel, item -> item));

        return reultMap;
    }


    public static void loadDictData() {

        CompletableFuture.supplyAsync(() -> {
            return Forest.client(DictDataApi.class).getSimpleDictDataList().getCheckedData();
        }).thenAcceptAsync(result -> {
            // 按 type 属性分组
            Map<String, List<DictDataSimpleRespVO>> groupedByType = result.stream()
                    .collect(Collectors.groupingBy(DictDataSimpleRespVO::getDictType));

            setDictDataListMap(groupedByType);
        }, SwingUtilities::invokeLater).exceptionally(throwable -> {
            SwingUtilities.invokeLater(() -> {
                SwingExceptionHandler.handle(throwable);
            });
            return null;
        });


    }

    public static MenuRefrestObservable getMenuRefreshObservable() {
        return menuRefreshObservable;
    }
}
