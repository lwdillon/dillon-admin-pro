package com.lw.dillon.admin.module.bpm.dal.mysql.definition;

import com.lw.dillon.admin.framework.common.pojo.PageResult;
import com.lw.dillon.admin.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.lw.dillon.admin.framework.mybatis.core.mapper.BaseMapperX;
import com.lw.dillon.admin.module.bpm.controller.admin.definition.vo.expression.BpmProcessExpressionPageReqVO;
import com.lw.dillon.admin.module.bpm.dal.dataobject.definition.BpmProcessExpressionDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * BPM 流程表达式 Mapper
 *
 * @author 芋道源码
 */
@Mapper
public interface BpmProcessExpressionMapper extends BaseMapperX<BpmProcessExpressionDO> {

    default PageResult<BpmProcessExpressionDO> selectPage(BpmProcessExpressionPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<BpmProcessExpressionDO>()
                .likeIfPresent(BpmProcessExpressionDO::getName, reqVO.getName())
                .eqIfPresent(BpmProcessExpressionDO::getStatus, reqVO.getStatus())
                .betweenIfPresent(BpmProcessExpressionDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(BpmProcessExpressionDO::getId));
    }

}