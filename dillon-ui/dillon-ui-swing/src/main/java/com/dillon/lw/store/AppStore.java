package com.dillon.lw.store;

import com.dillon.lw.api.system.DictDataApi;
import com.dillon.lw.exception.SwingExceptionHandler;
import com.dillon.lw.module.system.controller.admin.auth.vo.AuthLoginRespVO;
import com.dillon.lw.module.system.controller.admin.auth.vo.AuthPermissionInfoRespVO;
import com.dillon.lw.module.system.controller.admin.dict.vo.data.DictDataSimpleRespVO;
import com.dillon.lw.swing.rx.SwingSchedulers;
import com.dillon.lw.utils.DictTypeEnum;
import com.dtflys.forest.Forest;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;
import lombok.extern.slf4j.Slf4j;

import java.awt.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import javax.swing.*;

@Slf4j
public class AppStore {

    private static AuthLoginRespVO authLoginRespVO;
    private static AuthPermissionInfoRespVO authPermissionInfoRespVO;

    private static Map<String, List<DictDataSimpleRespVO>> dictDataListMap;
    private static final String EMPTY_TOKEN = "";
    private static final String EMPTY_VIEW_TEXT = "暂无投运";

    /**
     * 获取功能面板（兼容历史拼写）。
     * 建议新代码使用 {@link #getNavigationPanel(String)}。
     */
    @Deprecated
    public static Container getNavigatonPanel(String className) {
        return getNavigationPanel(className);
    }

    /**
     * 通过类名反射创建导航面板。
     */
    public static Container getNavigationPanel(String className) {
        try {
            Class<?> clazz = Class.forName(className);
            JComponent container = (JComponent) clazz.getDeclaredConstructor().newInstance();
            container.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
            container.setOpaque(false);
            return container;
        } catch (Exception ex) {
            log.error("获取功能面板失败: className={}", className, ex);
            return new JLabel(EMPTY_VIEW_TEXT, JLabel.CENTER);
        }
    }

    public static void setAuthLoginRespVO(AuthLoginRespVO authLoginRespVO) {
        AppStore.authLoginRespVO = authLoginRespVO;
    }

    public static String getAccessToken() {
        return authLoginRespVO == null ? EMPTY_TOKEN : authLoginRespVO.getAccessToken();
    }

    public static String getRefreshToken() {
        return authLoginRespVO == null ? EMPTY_TOKEN : authLoginRespVO.getRefreshToken();
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
        return authPermissionInfoRespVO == null ? null : authPermissionInfoRespVO.getUser();
    }

    /**
     * 操作权限数组
     *
     * @return {@link Set }<{@link String }>
     */
    public static Set<String> getPermissions() {
        return authPermissionInfoRespVO == null ? null : authPermissionInfoRespVO.getPermissions();
    }


    /**
     * 角色标识数组
     *
     * @return {@link Set }<{@link String }>
     */
    public static Set<String> getRoles() {
        return authPermissionInfoRespVO == null ? null : authPermissionInfoRespVO.getRoles();
    }

    /**
     * 菜单树
     *
     * @return {@link Set }<{@link String }>
     */
    public static List<AuthPermissionInfoRespVO.MenuVO> getMenus() {
        return authPermissionInfoRespVO == null ? null : authPermissionInfoRespVO.getMenus();
    }

    public static Map<String, List<DictDataSimpleRespVO>> getDictDataListMap() {
        return dictDataListMap;
    }

    /**
     * 清理当前会话缓存（用于退出登录或会话失效）
     */
    public static void clearSession() {
        authLoginRespVO = null;
        authPermissionInfoRespVO = null;
        dictDataListMap = null;
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
        Map<String, DictDataSimpleRespVO> resultMap = dictDataSimpleRespVOList.stream()
                .collect(Collectors.toMap(DictDataSimpleRespVO::getValue, item -> item));

        return resultMap;
    }

    public static Map<String, DictDataSimpleRespVO> getDictDataMap(DictTypeEnum dictType) {
        List<DictDataSimpleRespVO> dictDataSimpleRespVOList = dictDataListMap.get(dictType.getType());

        // 将 List 转换为 Map，使用 id 作为键
        Map<String, DictDataSimpleRespVO> resultMap = dictDataSimpleRespVOList.stream()
                .collect(Collectors.toMap(DictDataSimpleRespVO::getLabel, item -> item));

        return resultMap;
    }


    public static void loadDictData() {
        Single
                /*
                 * 字典数据是全局缓存，但接口调用本身仍是同步请求，
                 * 因此这里用 RxJava 把它切到 IO 线程，再回到 EDT 写入全局状态。
                 */
                .fromCallable(() -> Forest.client(DictDataApi.class).getSimpleDictDataList().getCheckedData())
                .subscribeOn(Schedulers.io())
                .observeOn(SwingSchedulers.edt())
                .subscribe(result -> {
                    Map<String, List<DictDataSimpleRespVO>> groupedByType = result.stream()
                            .collect(Collectors.groupingBy(DictDataSimpleRespVO::getDictType));
                    setDictDataListMap(groupedByType);
                }, SwingExceptionHandler::handle);
    }
}
