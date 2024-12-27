package com.lw.fx.store;

import com.lw.dillon.admin.framework.common.pojo.CommonResult;
import com.lw.dillon.admin.module.system.controller.admin.auth.vo.AuthPermissionInfoRespVO;
import com.lw.dillon.admin.module.system.controller.admin.dict.vo.data.DictDataSimpleRespVO;
import com.lw.fx.request.Request;
import com.lw.ui.request.api.system.DictDataFeign;
import com.lw.ui.utils.DictTypeEnum;
import io.datafx.core.concurrent.ProcessChain;
import org.kordamp.ikonli.feather.Feather;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

public class AppStore {
    private static Map<String, List<DictDataSimpleRespVO>> dictDataListMap;
    private static AuthPermissionInfoRespVO authPermissionInfoRespVO;

    protected static final Random RANDOM = new Random();

    private static String token;

    public static String getToken() {
        return token;
    }

    public static String userTheme;

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

    public static String getUserTheme() {
        return userTheme;
    }

    public static void setUserTheme(String userTheme) {
        AppStore.userTheme = userTheme;
    }

    public static void loadDictData() {


        ProcessChain.create().addSupplierInExecutor(() -> {
                    CommonResult<List<DictDataSimpleRespVO>> commonResult = Request.connector(DictDataFeign.class).getSimpleDictDataList();

                    // 按 type 属性分组
                    Map<String, List<DictDataSimpleRespVO>> groupedByType = commonResult.getData().stream()
                            .collect(Collectors.groupingBy(DictDataSimpleRespVO::getDictType));

                    return groupedByType;
                }).addConsumerInPlatformThread(dictDataListMap -> setDictDataListMap(dictDataListMap))
                .run();

    }


}
