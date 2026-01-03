package com.dillon.lw.fx.store;

import com.dillon.lw.api.system.DictDataApi;
import com.dillon.lw.fx.http.PayLoad;
import com.dillon.lw.module.system.controller.admin.auth.vo.AuthPermissionInfoRespVO;
import com.dillon.lw.module.system.controller.admin.dict.vo.data.DictDataSimpleRespVO;
import com.dillon.lw.utils.DictTypeEnum;
import com.dtflys.forest.Forest;
import javafx.application.Platform;
import org.kordamp.ikonli.feather.Feather;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class AppStore {
    private static Map<String, List<DictDataSimpleRespVO>> dictDataListMap;
    private static AuthPermissionInfoRespVO authPermissionInfoRespVO;
    private static boolean DARK_MODE = false;

    protected static final Random RANDOM = new Random();

    private static String token;

    public static String getToken() {
        return token;
    }


    public static void setToken(String token) {
        AppStore.token = token;
    }


    public static Feather randomIcon() {
        return Feather.values()[RANDOM.nextInt(Feather.values().length)];
    }

    public static AuthPermissionInfoRespVO getAuthPermissionInfoRespVO() {
        return authPermissionInfoRespVO;
    }

    public static void setAuthPermissionInfoRespVO(AuthPermissionInfoRespVO authPermissionInfoRespVO) {
        AppStore.authPermissionInfoRespVO = authPermissionInfoRespVO;
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

    public static AuthPermissionInfoRespVO.UserVO getUser() {
        return getAuthPermissionInfoRespVO().getUser();
    }

    public static Map<String, DictDataSimpleRespVO> getDictDataLabelMap(DictTypeEnum dictType) {
        List<DictDataSimpleRespVO> dictDataSimpleRespVOList = dictDataListMap.get(dictType.getType());

        // 将 List 转换为 Map，使用 id 作为键
        Map<String, DictDataSimpleRespVO> reultMap = dictDataSimpleRespVOList.stream()
                .collect(Collectors.toMap(DictDataSimpleRespVO::getLabel, item -> item));

        return reultMap;
    }

    public static Map<String, DictDataSimpleRespVO> getDictDataValueMap(DictTypeEnum dictType) {
        List<DictDataSimpleRespVO> dictDataSimpleRespVOList = dictDataListMap.get(dictType.getType());

        // 将 List 转换为 Map，使用 id 作为键
        Map<String, DictDataSimpleRespVO> reultMap = dictDataSimpleRespVOList.stream()
                .collect(Collectors.toMap(DictDataSimpleRespVO::getValue, item -> item));

        return reultMap;
    }



    public static void loadDictData() {

        CompletableFuture.supplyAsync(() -> {
            return new PayLoad<List<DictDataSimpleRespVO>>().apply(Forest.client(DictDataApi.class).getSimpleDictDataList());
        }).thenAcceptAsync(commonResult -> {
            // 按 type 属性分组
            Map<String, List<DictDataSimpleRespVO>> groupedByType = commonResult.stream()
                    .collect(Collectors.groupingBy(DictDataSimpleRespVO::getDictType));

            setDictDataListMap(groupedByType);
        }, Platform::runLater).exceptionally(throwable -> {
            // 处理错误
            throwable.printStackTrace();
            return null;
        });

    }

    public static boolean isDarkMode() {
        return DARK_MODE;
    }

    public static void setDarkMode(boolean darkMode) {
        DARK_MODE = darkMode;
    }
}
