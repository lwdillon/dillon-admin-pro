package com.lw.fx.view.system.dept;

import cn.hutool.core.util.StrUtil;
import com.lw.dillon.admin.framework.common.pojo.CommonResult;
import com.lw.dillon.admin.module.system.controller.admin.dept.vo.dept.DeptListReqVO;
import com.lw.dillon.admin.module.system.controller.admin.dept.vo.dept.DeptRespVO;
import com.lw.fx.request.Request;
import com.lw.ui.request.api.system.DeptFeign;
import de.saxsys.mvvmfx.SceneLifecycle;
import de.saxsys.mvvmfx.ViewModel;
import io.datafx.core.concurrent.ProcessChain;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.TreeItem;

import java.util.HashMap;
import java.util.Map;


/**
 * 菜单管理视图模型
 *
 * @author wenli
 * @date 2023/02/15
 */
public class DeptManageViewModel implements ViewModel, SceneLifecycle {
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

    public void initialize() {

    }

    public void someAction() {
        publish(OPEN_ALERT, "Some Error has happend");
    }


    @Override
    public void onViewAdded() {
        System.err.println("------add");
    }


    @Override
    public void onViewRemoved() {
        unsubscribe(OPEN_ALERT, (s, objects) -> {
        });
    }


    public ObjectProperty<TreeItem<DeptRespVO>> treeItemObjectPropertyProperty() {
        return treeItemObjectProperty;
    }

    /**
     * 获取菜单列表
     */

    public void query() {
        String status = statusText.getValue();
        DeptListReqVO deptListReqVO = new DeptListReqVO();
        if (StrUtil.isNotBlank(searchText.getValue())) {
            deptListReqVO.setName(searchText.getValue());
        }
        deptListReqVO.setStatus(StrUtil.equals("全部", status) ? null : (StrUtil.equals("开启", status) ? 0 : 1));

        ProcessChain.create()
                .addSupplierInExecutor(() -> {
                    return Request.connector(DeptFeign.class).getDeptList(deptListReqVO);
                })
                .addConsumerInPlatformThread(r -> {

                    if (r.isSuccess()) {
                        // 创建根节点（空的或具有实际数据）
                        TreeItem<DeptRespVO> rootItem = new TreeItem<>(new DeptRespVO());
                        rootItem.setExpanded(true);
                        // 将菜单列表转换为树形结构
                        Map<Long, TreeItem<DeptRespVO>> itemMap = new HashMap<>();

                        for (DeptRespVO dept : r.getData()) {
                            TreeItem<DeptRespVO> treeItem = new TreeItem<>(dept);
                            itemMap.put(dept.getId(), treeItem);

                        }

                        r.getData().forEach(deptRespVO -> {
                            TreeItem<DeptRespVO> parentNode = itemMap.get(deptRespVO.getParentId());
                            TreeItem<DeptRespVO> childNode = itemMap.get(deptRespVO.getId());
                            if (parentNode != null) {
                                parentNode.getChildren().add(childNode);
                            }else {
                                rootItem.getChildren().add(childNode);
                            }


                        });


                        treeItemObjectProperty.set(rootItem);
                    } else {

                    }
                })
                .onException(e -> {
                    e.printStackTrace();
                })
                .withFinal(() -> {
                })
                .run();

    }


    public CommonResult<Boolean> remove(Long deptId) {
       return Request.connector(DeptFeign.class).deleteDept(deptId);
    }

    public void rest() {
        searchTextProperty().setValue("");
        statusText.setValue("全部");
    }


}
