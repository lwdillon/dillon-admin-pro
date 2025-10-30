package com.dillon.lw.fx.view.system.dept;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.dillon.lw.framework.common.pojo.CommonResult;
import com.dillon.lw.module.system.controller.admin.dept.vo.dept.DeptListReqVO;
import com.dillon.lw.module.system.controller.admin.dept.vo.dept.DeptRespVO;
import com.dillon.lw.module.system.controller.admin.user.vo.user.UserSimpleRespVO;
import com.google.common.eventbus.Subscribe;
import com.dillon.lw.api.system.DeptApi;
import com.dillon.lw.api.system.UserApi;
import com.dillon.lw.fx.eventbus.EventBusCenter;
import com.dillon.lw.fx.eventbus.event.MessageEvent;
import com.dillon.lw.fx.eventbus.event.RefreshEvent;
import com.dillon.lw.fx.eventbus.event.UpdateDataEvent;
import com.dillon.lw.fx.http.PayLoad;
import com.dillon.lw.fx.http.Request;
import com.dillon.lw.fx.mvvm.base.BaseViewModel;
import com.dillon.lw.fx.utils.MessageType;
import com.dillon.lw.fx.view.layout.ConfirmDialog;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.TreeItem;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;


/**
 * 菜单管理视图模型
 *
 * @author wenli
 * @date 2023/02/15
 */
public class DeptManageViewModel extends BaseViewModel {
    public final static String OPEN_ALERT = "OPEN_ALERT";

    private ObjectProperty<TreeItem<DeptRespVO>> treeItemObjectProperty = new SimpleObjectProperty<>(new TreeItem<>());
    private SimpleStringProperty searchText = new SimpleStringProperty("");
    private SimpleStringProperty statusText = new SimpleStringProperty("全部");

    public String getSearchText() {
        return searchText.get();
    }

    public SimpleStringProperty searchTextProperty() {
        return searchText;
    }

    public String getStatusText() {
        return statusText.get();
    }

    public SimpleStringProperty statusTextProperty() {
        return statusText;
    }

    public ObjectProperty<TreeItem<DeptRespVO>> treeItemObjectPropertyProperty() {
        return treeItemObjectProperty;
    }

    public Map<Long, UserSimpleRespVO> userMap = new HashMap<>();


    /**
     * 获取菜单列表
     */

    public void query() {
        treeItemObjectProperty.set(new TreeItem<>());
        String status = statusText.getValue();
        DeptListReqVO deptListReqVO = new DeptListReqVO();
        if (StrUtil.isNotBlank(searchText.getValue())) {
            deptListReqVO.setName(searchText.getValue());
        }
        deptListReqVO.setStatus(StrUtil.equals("全部", status) ? null : (StrUtil.equals("开启", status) ? 0 : 1));
        Map<String, Object> queryMap = BeanUtil.beanToMap(deptListReqVO, false, true);


        Observable<CommonResult<List<UserSimpleRespVO>>> userList = Request.getInstance().create(UserApi.class).getSimpleUserList();
        Observable<CommonResult<List<DeptRespVO>>> deptList = Request.getInstance().create(DeptApi.class).getDeptList(queryMap);


        // 使用 zip 合并两个请求结果
        Disposable disposable = Observable.zip(userList, deptList,
                        (user, dept) -> {
                            // 返回你需要组合的结果

                            Map<String, Object> map = new HashMap<>();
                            map.put("user", user.getData());
                            map.put("dept", dept.getData());
                            return map;
                        })
                .subscribeOn(Schedulers.io()) // 在 IO 线程中请求
                .observeOn(Schedulers.from(Platform::runLater)) // JavaFX UI线程中观察（需引入 JavaFxScheduler）
                .subscribe(result -> {

                    List<DeptRespVO> data = (List<DeptRespVO>) result.get("dept");
                    List<UserSimpleRespVO> userListData = (List<UserSimpleRespVO>) result.get("user");

                    if (userListData.size() > 0) {
                        userMap = userListData.stream()
                                .collect(Collectors.toMap(UserSimpleRespVO::getId, Function.identity()));
                    }
                    // 创建根节点（空的或具有实际数据）
                    TreeItem<DeptRespVO> rootItem = new TreeItem<>(new DeptRespVO());
                    rootItem.setExpanded(true);
                    // 将菜单列表转换为树形结构
                    Map<Long, TreeItem<DeptRespVO>> itemMap = new HashMap<>();

                    for (DeptRespVO dept : data) {
                        TreeItem<DeptRespVO> treeItem = new TreeItem<>(dept);
                        itemMap.put(dept.getId(), treeItem);

                    }

                    data.forEach(deptRespVO -> {
                        TreeItem<DeptRespVO> parentNode = itemMap.get(deptRespVO.getParentId());
                        TreeItem<DeptRespVO> childNode = itemMap.get(deptRespVO.getId());
                        if (parentNode != null) {
                            parentNode.getChildren().add(childNode);
                        } else {
                            rootItem.getChildren().add(childNode);
                        }


                    });
                    treeItemObjectProperty.set(rootItem);
                }, throwable -> {
                    // 错误处理
                    throwable.printStackTrace();
                });


    }


    public void deleteDept(Long deptId, ConfirmDialog confirmDialog) {
        Request.getInstance().create(DeptApi.class).deleteDept(deptId)
                .subscribeOn(Schedulers.io())
                .map(new PayLoad<>())
                .observeOn(Schedulers.from(Platform::runLater))
                .subscribe(commonResult -> {

                    EventBusCenter.get().post(new MessageEvent("删除成功！", MessageType.SUCCESS));
                    EventBusCenter.get().post(new UpdateDataEvent("更新部门列表"));
                    confirmDialog.close();
                }, throwable -> {
                    throwable.printStackTrace();
                });
    }

    public void rest() {
        searchTextProperty().setValue("");
        statusText.setValue("全部");
    }

    public Map<Long, UserSimpleRespVO> getUserMap() {
        return userMap;
    }

    @Subscribe
    private void updateData(UpdateDataEvent menuEvent) {
        Platform.runLater(() -> {
            if ("更新部门列表".equals(menuEvent.getMessage())) {
                query();
            }
        });
    }

    @Subscribe
    private void refresh(RefreshEvent event) {
        Platform.runLater(() -> query());
    }


}
