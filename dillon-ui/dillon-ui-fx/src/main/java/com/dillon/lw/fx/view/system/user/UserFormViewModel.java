package com.dillon.lw.fx.view.system.user;

import cn.hutool.core.bean.BeanUtil;
import com.dillon.lw.api.system.DeptApi;
import com.dillon.lw.api.system.PostApi;
import com.dillon.lw.api.system.UserApi;
import com.dillon.lw.fx.eventbus.EventBusCenter;
import com.dillon.lw.fx.eventbus.event.MessageEvent;
import com.dillon.lw.fx.eventbus.event.UpdateDataEvent;
import com.dillon.lw.fx.http.PayLoad;
import com.dillon.lw.fx.mvvm.base.BaseViewModel;
import com.dillon.lw.fx.mvvm.mapping.ModelWrapper;
import com.dillon.lw.fx.utils.MessageType;
import com.dillon.lw.fx.view.layout.ConfirmDialog;
import com.dillon.lw.module.system.controller.admin.dept.vo.dept.DeptSimpleRespVO;
import com.dillon.lw.module.system.controller.admin.dept.vo.post.PostSimpleRespVO;
import com.dillon.lw.module.system.controller.admin.user.vo.user.UserRespVO;
import com.dillon.lw.module.system.controller.admin.user.vo.user.UserSaveReqVO;
import com.dtflys.forest.Forest;
import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;

import java.util.*;
import java.util.concurrent.CompletableFuture;

public class UserFormViewModel extends BaseViewModel {

    private ObjectProperty<TreeItem<DeptSimpleRespVO>> deptTreeRoot = new SimpleObjectProperty<>();
    private ObjectProperty<TreeItem<DeptSimpleRespVO>> selectTreeItem = new SimpleObjectProperty<>();
    private ObjectProperty<ObservableList<PostSimpleRespVO>> postItems = new SimpleObjectProperty<>();
    private ObservableList<PostSimpleRespVO> selectPostItems = FXCollections.observableArrayList();

    private ModelWrapper<UserSaveReqVO> wrapper = new ModelWrapper<>();


    public UserFormViewModel() {

    }


    public void query(Long id) {

        if (id == null) {
            setUser(new UserSaveReqVO());
            CompletableFuture.supplyAsync(() -> {
                return new PayLoad<List<DeptSimpleRespVO>>().apply(Forest.client(DeptApi.class).getSimpleDeptList());
            }).thenAcceptAsync(deptList -> {
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

            }, Platform::runLater).thenComposeAsync(v -> {
                return CompletableFuture.supplyAsync(() -> {
                    return new PayLoad<List<PostSimpleRespVO>>().apply(Forest.client(PostApi.class).getSimplePostList());
                });
            }).thenAcceptAsync(postList -> {
                ObservableList<PostSimpleRespVO> list = FXCollections.observableArrayList(postList);
                Set<Long> postIds = wrapper.get().getPostIds();
                if (postIds != null) {
                    for (PostSimpleRespVO respVO : list) {
                        if (postIds.contains(respVO.getId())) {
                            selectPostItems.add(respVO);
                        }
                    }
                }

                postItems.set(list);
            }, Platform::runLater).exceptionally(throwable -> {
                throwable.printStackTrace();
                EventBusCenter.get().post(new MessageEvent("查询岗位失败", MessageType.DANGER));
                return null;
            });
        } else {

            CompletableFuture.supplyAsync(() -> {
                return new PayLoad<UserRespVO>().apply(Forest.client(UserApi.class).getUser(id));
            }).thenAcceptAsync(user -> {
                if (user == null) {
                    setUser(new UserSaveReqVO());
                } else {
                    UserSaveReqVO reqVO = new UserSaveReqVO();
                    BeanUtil.copyProperties(user, reqVO);
                    setUser(reqVO);
                }
            }, Platform::runLater).thenComposeAsync(v -> {
                return CompletableFuture.supplyAsync(() -> {
                    return new PayLoad<List<DeptSimpleRespVO>>().apply(Forest.client(DeptApi.class).getSimpleDeptList());
                });
            }).thenAcceptAsync(deptList -> {
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

            }, Platform::runLater).thenComposeAsync(v -> {
                return CompletableFuture.supplyAsync(() -> {
                    return new PayLoad<List<PostSimpleRespVO>>().apply(Forest.client(PostApi.class).getSimplePostList());
                });
            }).thenAcceptAsync(postList -> {
                ObservableList<PostSimpleRespVO> list = FXCollections.observableArrayList(postList);
                Set<Long> postIds = wrapper.get().getPostIds();
                if (postIds != null) {
                    for (PostSimpleRespVO respVO : list) {
                        if (postIds.contains(respVO.getId())) {
                            selectPostItems.add(respVO);
                        }
                    }
                }

                postItems.set(list);
            }, Platform::runLater).exceptionally(throwable -> {
                throwable.printStackTrace();
                EventBusCenter.get().post(new MessageEvent("查询岗位失败", MessageType.DANGER));
                return null;
            });

        }
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


        CompletableFuture.supplyAsync(() -> {
            return new PayLoad<Long>().apply(Forest.client(UserApi.class).createUser(getUserSaveReqVO()));
        }).thenAcceptAsync(data -> {
            EventBusCenter.get().post(new MessageEvent("操作成功", MessageType.SUCCESS));
            EventBusCenter.get().post(new UpdateDataEvent("更新用户列表"));
            confirmDialog.close();
        }, Platform::runLater).exceptionally(throwable -> {
            throwable.printStackTrace();
            return null;
        });
    }

    public void updateUser(ConfirmDialog confirmDialog) {

        CompletableFuture.supplyAsync(() -> {
            return new PayLoad<Boolean>().apply(Forest.client(UserApi.class).updateUser(getUserSaveReqVO()));
        }).thenAcceptAsync(data -> {
            EventBusCenter.get().post(new MessageEvent("操作成功", MessageType.SUCCESS));
            EventBusCenter.get().post(new UpdateDataEvent("更新用户列表"));
            confirmDialog.close();
        }, Platform::runLater).exceptionally(throwable -> {
            throwable.printStackTrace();
            return null;
        });
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

}
