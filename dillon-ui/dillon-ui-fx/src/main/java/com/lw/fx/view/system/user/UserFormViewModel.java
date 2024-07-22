package com.lw.fx.view.system.user;

import cn.hutool.core.bean.BeanUtil;
import com.lw.dillon.admin.framework.common.pojo.CommonResult;
import com.lw.dillon.admin.module.system.controller.admin.dept.vo.dept.DeptSimpleRespVO;
import com.lw.dillon.admin.module.system.controller.admin.dept.vo.post.PostSimpleRespVO;
import com.lw.dillon.admin.module.system.controller.admin.user.vo.user.UserRespVO;
import com.lw.dillon.admin.module.system.controller.admin.user.vo.user.UserSaveReqVO;
import com.lw.fx.request.Request;
import com.lw.ui.request.api.system.DeptFeign;
import com.lw.ui.request.api.system.PostFeign;
import com.lw.ui.request.api.system.UserFeign;
import de.saxsys.mvvmfx.SceneLifecycle;
import de.saxsys.mvvmfx.ViewModel;
import de.saxsys.mvvmfx.utils.mapping.ModelWrapper;
import io.datafx.core.concurrent.ProcessChain;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class UserFormViewModel implements ViewModel, SceneLifecycle {

    private ObjectProperty<TreeItem<DeptSimpleRespVO>> deptTreeRoot = new SimpleObjectProperty<>();
    private ObjectProperty<TreeItem<DeptSimpleRespVO>> selectTreeItem = new SimpleObjectProperty<>();
    private ObjectProperty<ObservableList<PostSimpleRespVO>> postItems = new SimpleObjectProperty<>();
    private ObservableList<PostSimpleRespVO> selectPostItems = FXCollections.observableArrayList();

    public BooleanProperty create = new SimpleBooleanProperty(false);
    private ModelWrapper<UserSaveReqVO> wrapper = new ModelWrapper<>();

    public UserFormViewModel() {

    }


    public void query(Long id) {

        ProcessChain.create()
                .addSupplierInExecutor(() -> {
                    if (id == null) {
                        return new UserRespVO();
                    }
                    return Request.connector(UserFeign.class).getUser(id).getData();
                })
                .addConsumerInPlatformThread(r -> {
                    UserSaveReqVO reqVO = new UserSaveReqVO();
                    BeanUtil.copyProperties(r, reqVO);
                    setUser(reqVO);

                }).addSupplierInExecutor(() -> Request.connector(DeptFeign.class).getSimpleDeptList().getData())
                .addConsumerInPlatformThread(deptSimpleRespVOS -> {

                    DeptSimpleRespVO respVO = new DeptSimpleRespVO();
                    respVO.setId(0L);
                    respVO.setName("主类目");

                    TreeItem<DeptSimpleRespVO> root = new TreeItem<>(respVO);
                    root.setExpanded(true);
                    // Build the tree
                    Map<Long, TreeItem<DeptSimpleRespVO>> nodeMap = new HashMap<>();
                    nodeMap.put(0l, root); // Root node

                    for (DeptSimpleRespVO simpleRespVO : deptSimpleRespVOS) {

                        TreeItem<DeptSimpleRespVO> node = new TreeItem<DeptSimpleRespVO>(simpleRespVO);
                        nodeMap.put(simpleRespVO.getId(), node);

                    }

                    deptSimpleRespVOS.forEach(menuSimpleRespVO -> {

                        TreeItem<DeptSimpleRespVO> parentNode = nodeMap.get(menuSimpleRespVO.getParentId());
                        TreeItem<DeptSimpleRespVO> childNode = nodeMap.get(menuSimpleRespVO.getId());
                        if (parentNode != null) {
                            parentNode.getChildren().add(childNode);
                        }


                    });

                    if (nodeMap.get(wrapper.get().getDeptId()) != null) {
                        selectTreeItem.setValue(nodeMap.get(wrapper.get().getDeptId()));
                    } else {
                        selectTreeItem.setValue(root);
                    }
                    deptTreeRoot.set(root);
                }).addSupplierInExecutor(() -> Request.connector(PostFeign.class).getSimplePostList().getData())
                .addConsumerInPlatformThread(postSimpleRespVOS -> {
                    ObservableList<PostSimpleRespVO> list = FXCollections.observableArrayList();
                    list.addAll(postSimpleRespVOS);
                    for (PostSimpleRespVO respVO : postSimpleRespVOS) {
                        if ((wrapper.get().getPostIds() != null)) {
                            if (wrapper.get().getPostIds().contains(respVO.getId())) {
                                selectPostItems.add(respVO);
                            }
                        }

                    }
                    postItems.set(list);

                })
                .onException(e -> e.printStackTrace())
                .run();
    }

    /**
     * 系统设置菜单
     */
    public void setUser(UserSaveReqVO userRespVO) {

        wrapper.set(userRespVO);
        wrapper.reload();
    }

    public ModelWrapper<UserSaveReqVO> getWrapper() {
        return wrapper;
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

    public StringProperty nicknameProperty() {
        return wrapper.field("nickname", UserSaveReqVO::getNickname, UserSaveReqVO::setNickname, "");
    }

    public StringProperty usernameProperty() {
        return wrapper.field("username", UserSaveReqVO::getUsername, UserSaveReqVO::setUsername, "");
    }

    public StringProperty passwordProperty() {
        return wrapper.field("password", UserSaveReqVO::getPassword, UserSaveReqVO::setPassword, "");
    }

    public LongProperty deptIdProperty() {
        return wrapper.field("deptId", UserSaveReqVO::getDeptId, UserSaveReqVO::setDeptId);
    }

    public StringProperty mobileProperty() {
        return wrapper.field("mobile", UserSaveReqVO::getMobile, UserSaveReqVO::setMobile, "");
    }

    public StringProperty emailProperty() {
        return wrapper.field("email", UserSaveReqVO::getEmail, UserSaveReqVO::setEmail, "");
    }

    public IntegerProperty sexProperty() {
        return wrapper.field("sex", UserSaveReqVO::getSex, UserSaveReqVO::setSex, -1);
    }

    public SetProperty<Long> postIdsProperty() {
        return wrapper.field("postIds", UserSaveReqVO::getPostIds, UserSaveReqVO::setPostIds);
    }

    public StringProperty remarkProperty() {
        return wrapper.field("remark", UserSaveReqVO::getRemark, UserSaveReqVO::setRemark);
    }

    public boolean isCreate() {
        return create.get();
    }

    public BooleanProperty createProperty() {
        return create;
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

    public CommonResult saveUser(boolean isAdd) {

        if (isAdd) {
            return Request.connector(UserFeign.class).createUser(getUserSaveReqVO());
        } else {
            return Request.connector(UserFeign.class).updateUser(getUserSaveReqVO());
        }
    }

    @Override
    public void onViewAdded() {

    }

    @Override
    public void onViewRemoved() {

    }
}
