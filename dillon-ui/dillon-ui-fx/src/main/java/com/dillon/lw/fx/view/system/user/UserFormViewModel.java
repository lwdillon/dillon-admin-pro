package com.dillon.lw.fx.view.system.user;

import cn.hutool.core.bean.BeanUtil;
import com.dillon.lw.api.system.DeptApi;
import com.dillon.lw.api.system.PostApi;
import com.dillon.lw.api.system.UserApi;
import com.dillon.lw.fx.DefaultExceptionHandler;
import com.dillon.lw.fx.eventbus.EventBusCenter;
import com.dillon.lw.fx.eventbus.event.MessageEvent;
import com.dillon.lw.fx.eventbus.event.UpdateDataEvent;
import com.dillon.lw.fx.mvvm.base.BaseViewModel;
import com.dillon.lw.fx.mvvm.mapping.ModelWrapper;
import com.dillon.lw.fx.rx.FxSchedulers;
import com.dillon.lw.fx.rx.FxRx;
import com.dillon.lw.fx.utils.MessageType;
import com.dillon.lw.fx.view.layout.ConfirmDialog;
import com.dillon.lw.module.system.controller.admin.dept.vo.dept.DeptSimpleRespVO;
import com.dillon.lw.module.system.controller.admin.dept.vo.post.PostSimpleRespVO;
import com.dillon.lw.module.system.controller.admin.user.vo.user.UserSaveReqVO;
import com.dtflys.forest.Forest;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class UserFormViewModel extends BaseViewModel {

    private ObjectProperty<TreeItem<DeptSimpleRespVO>> deptTreeRoot = new SimpleObjectProperty<>();
    private ObjectProperty<TreeItem<DeptSimpleRespVO>> selectTreeItem = new SimpleObjectProperty<>();
    private ObjectProperty<ObservableList<PostSimpleRespVO>> postItems = new SimpleObjectProperty<>();
    private ObservableList<PostSimpleRespVO> selectPostItems = FXCollections.observableArrayList();
    private ObjectProperty<Boolean> isAdd = new SimpleObjectProperty<>(false);

    private ModelWrapper<UserSaveReqVO> wrapper = new ModelWrapper<>();


    public UserFormViewModel() {

    }


    public void query(Long id) {

        if (id == null) {
            setIsAdd(true);
            setUser(new UserSaveReqVO());
            Single
                    /*
                     * 新增用户表单初始化依赖部门树和岗位列表两份数据，
                     * 这里直接并行拉取，再在 JavaFX UI 线程一次性回填两个选择器。
                     */
                    .zip(
                            Single.fromCallable(() -> Forest.client(DeptApi.class).getSimpleDeptList().getCheckedData()),
                            Single.fromCallable(() -> Forest.client(PostApi.class).getSimplePostList().getCheckedData()),
                            (deptList, postList) -> {
                                Map<String, Object> result = new HashMap<String, Object>();
                                result.put("depts", deptList);
                                result.put("posts", postList);
                                return result;
                            }
                    )
                    .subscribeOn(Schedulers.io())
                    .observeOn(FxSchedulers.fx())
                    .compose(FxRx.bindTo(this))
                    .subscribe(result -> {
                        java.util.List<DeptSimpleRespVO> deptList = (java.util.List<DeptSimpleRespVO>) result.get("depts");
                        java.util.List<PostSimpleRespVO> postList = (java.util.List<PostSimpleRespVO>) result.get("posts");
                        fillDeptTree(deptList);
                        fillPostItems(postList);
                    }, DefaultExceptionHandler::handle);
        } else {
            setIsAdd(false);
            Single
                    /*
                     * 编辑用户时需要三份数据：用户详情、部门树、岗位列表。
                     * 用 zip 并行后统一回到 JavaFX UI 线程填表，避免 thenCompose 链层层嵌套。
                     */
                    .zip(
                            Single.fromCallable(() -> Forest.client(UserApi.class).getUser(id).getCheckedData()),
                            Single.fromCallable(() -> Forest.client(DeptApi.class).getSimpleDeptList().getCheckedData()),
                            Single.fromCallable(() -> Forest.client(PostApi.class).getSimplePostList().getCheckedData()),
                            (user, deptList, postList) -> {
                                Map<String, Object> result = new HashMap<String, Object>();
                                result.put("user", user);
                                result.put("depts", deptList);
                                result.put("posts", postList);
                                return result;
                            }
                    )
                    .subscribeOn(Schedulers.io())
                    .observeOn(FxSchedulers.fx())
                    .compose(FxRx.bindTo(this))
                    .subscribe(result -> {
                        Object user = result.get("user");
                        if (user == null) {
                            setUser(new UserSaveReqVO());
                        } else {
                            UserSaveReqVO reqVO = new UserSaveReqVO();
                            BeanUtil.copyProperties(user, reqVO);
                            setUser(reqVO);
                        }
                        fillDeptTree((java.util.List<DeptSimpleRespVO>) result.get("depts"));
                        fillPostItems((java.util.List<PostSimpleRespVO>) result.get("posts"));
                    }, DefaultExceptionHandler::handle);

        }
    }

    private void fillDeptTree(java.util.List<DeptSimpleRespVO> deptList) {
        DeptSimpleRespVO rootVO = new DeptSimpleRespVO();
        rootVO.setId(0L);
        rootVO.setName("主类目");

        TreeItem<DeptSimpleRespVO> root = new TreeItem<>(rootVO);
        root.setExpanded(true);

        Map<Long, TreeItem<DeptSimpleRespVO>> nodeMap = new HashMap<>();
        nodeMap.put(0L, root);
        for (DeptSimpleRespVO vo : deptList) {
            nodeMap.put(vo.getId(), new TreeItem<>(vo));
        }

        for (DeptSimpleRespVO vo : deptList) {
            TreeItem<DeptSimpleRespVO> parent = nodeMap.get(vo.getParentId());
            TreeItem<DeptSimpleRespVO> child = nodeMap.get(vo.getId());
            if (parent != null) {
                parent.getChildren().add(child);
            }
        }

        TreeItem<DeptSimpleRespVO> selected = nodeMap.getOrDefault(deptIdProperty().get(), root);
        selectTreeItem.setValue(selected);
        deptTreeRoot.set(root);
    }

    private void fillPostItems(java.util.List<PostSimpleRespVO> postList) {
        ObservableList<PostSimpleRespVO> list = FXCollections.observableArrayList(postList);
        selectPostItems.clear();
        Set<Long> postIds = wrapper.get().getPostIds();
        if (postIds != null) {
            for (PostSimpleRespVO respVO : list) {
                if (postIds.contains(respVO.getId())) {
                    selectPostItems.add(respVO);
                }
            }
        }
        postItems.set(list);
    }

    /**
     * 系统设置菜单
     */
    public void setUser(UserSaveReqVO userRespVO) {
        wrapper.set(userRespVO);
        wrapper.reload();
    }


    public UserSaveReqVO getUserSaveReqVO() {
        wrapper.commit();
        UserSaveReqVO saveReqVO = wrapper.get();

        Set<Long> postids = new HashSet<>();
        for (PostSimpleRespVO respVO : selectPostItems) {
            postids.add(respVO.getId());
        }
        if (postids.isEmpty() == false) {
            saveReqVO.setPostIds(postids);
        }

        if (selectTreeItem != null) {
            saveReqVO.setDeptId(selectTreeItem.getValue().getValue().getId());
        }


        return saveReqVO;
    }


    public TreeItem<DeptSimpleRespVO> getDeptTreeRoot() {
        return deptTreeRoot.get();
    }

    public ObjectProperty<TreeItem<DeptSimpleRespVO>> deptTreeRootProperty() {
        return deptTreeRoot;
    }

    public TreeItem<DeptSimpleRespVO> getSelectTreeItem() {
        return selectTreeItem.get();
    }

    public ObjectProperty<TreeItem<DeptSimpleRespVO>> selectTreeItemProperty() {
        return selectTreeItem;
    }

    public ObservableList<PostSimpleRespVO> getPostItems() {
        return postItems.get();
    }

    public ObjectProperty<ObservableList<PostSimpleRespVO>> postItemsProperty() {
        return postItems;
    }

    public ObservableList<PostSimpleRespVO> getSelectPostItems() {
        return selectPostItems;
    }

    public void addUser(ConfirmDialog confirmDialog) {


        Single
                .fromCallable(() -> Forest.client(UserApi.class).createUser(getUserSaveReqVO()).getCheckedData())
                .subscribeOn(Schedulers.io())
                .observeOn(FxSchedulers.fx())
                .compose(FxRx.bindTo(this))
                .subscribe(data -> {
                    EventBusCenter.get().post(new MessageEvent("操作成功", MessageType.SUCCESS));
                    EventBusCenter.get().post(new UpdateDataEvent("更新用户列表"));
                    confirmDialog.close();
                }, DefaultExceptionHandler::handle);
    }

    public void updateUser(ConfirmDialog confirmDialog) {

        Single
                .fromCallable(() -> Forest.client(UserApi.class).updateUser(getUserSaveReqVO()).getCheckedData())
                .subscribeOn(Schedulers.io())
                .observeOn(FxSchedulers.fx())
                .compose(FxRx.bindTo(this))
                .subscribe(data -> {
                    EventBusCenter.get().post(new MessageEvent("操作成功", MessageType.SUCCESS));
                    EventBusCenter.get().post(new UpdateDataEvent("更新用户列表"));
                    confirmDialog.close();
                }, DefaultExceptionHandler::handle);
    }

    public StringProperty usernameProperty() {
        return wrapper.field("username", UserSaveReqVO::getUsername, UserSaveReqVO::setUsername, "");
    }

    public StringProperty passwordProperty() {
        return wrapper.field("password", UserSaveReqVO::getPassword, UserSaveReqVO::setPassword, "");
    }

    public StringProperty nickNameProperty() {
        return wrapper.field("nickName", UserSaveReqVO::getNickname, UserSaveReqVO::setNickname, "");
    }

    public StringProperty emailProperty() {
        return wrapper.field("email", UserSaveReqVO::getEmail, UserSaveReqVO::setEmail, "");
    }

    public StringProperty mobileProperty() {
        return wrapper.field("mobile", UserSaveReqVO::getMobile, UserSaveReqVO::setMobile, "");
    }

    public StringProperty remarkProperty() {
        return wrapper.field("remark", UserSaveReqVO::getRemark, UserSaveReqVO::setRemark, "");
    }

    public IntegerProperty sexProperty() {
        return wrapper.field("sex", UserSaveReqVO::getSex, UserSaveReqVO::setSex, 0);
    }


    public SetProperty<Long> postIdsProperty() {
        return wrapper.field("postIds", UserSaveReqVO::getPostIds, UserSaveReqVO::setPostIds);
    }

    public LongProperty deptIdProperty() {
        return wrapper.field("deptId", UserSaveReqVO::getDeptId, UserSaveReqVO::setDeptId, 0L);
    }

    public Boolean getIsAdd() {
        return isAdd.get();
    }

    public ObjectProperty<Boolean> isAddProperty() {
        return isAdd;
    }

    public void setIsAdd(Boolean isAdd) {
        this.isAdd.set(isAdd);
    }
}
