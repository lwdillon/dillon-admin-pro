package com.dillon.lw.fx.store;

import com.dillon.lw.api.system.DictDataApi;
import com.dillon.lw.fx.DefaultExceptionHandler;
import com.dillon.lw.fx.rx.FxSchedulers;
import com.dillon.lw.module.system.controller.admin.auth.vo.AuthPermissionInfoRespVO;
import com.dillon.lw.module.system.controller.admin.dict.vo.data.DictDataSimpleRespVO;
import com.dillon.lw.utils.DictTypeEnum;
import com.dtflys.forest.Forest;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;
import org.kordamp.ikonli.feather.Feather;

import java.util.List;
import java.util.Map;
import java.util.Random;
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
        Single
                /*
                 * 字典缓存加载本质上是一次同步 HTTP 请求：
                 * 上游放到 IO 线程取数，按类型分组后的缓存回写再切回 JavaFX UI 线程。
                 */
                .fromCallable(() -> Forest.client(DictDataApi.class).getSimpleDictDataList().getCheckedData())
                .subscribeOn(Schedulers.io())
                .observeOn(FxSchedulers.fx())
                .subscribe(commonResult -> {
                    Map<String, List<DictDataSimpleRespVO>> groupedByType = commonResult.stream()
                            .collect(Collectors.groupingBy(DictDataSimpleRespVO::getDictType));
                    setDictDataListMap(groupedByType);
                }, DefaultExceptionHandler::handle);

    }

    public static boolean isDarkMode() {
        return DARK_MODE;
    }

    public static void setDarkMode(boolean darkMode) {
        DARK_MODE = darkMode;
    }
}
