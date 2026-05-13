package com.dillon.lw.module.infra.dal.mysql.demo.demo03.normal;

import com.dillon.lw.framework.common.pojo.PageResult;
import com.dillon.lw.framework.mybatis.core.mapper.BaseMapperX;
import com.dillon.lw.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.dillon.lw.module.infra.controller.admin.demo.demo03.normal.vo.Demo03StudentNormalPageReqVO;
import com.dillon.lw.module.infra.dal.dataobject.demo.demo03.Demo03StudentDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 学生 Mapper
 *
 * @author 芋道源码
 */
@Mapper
public interface Demo03StudentNormalMapper extends BaseMapperX<Demo03StudentDO> {

    default PageResult<Demo03StudentDO> selectPage(Demo03StudentNormalPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<Demo03StudentDO>()
                .likeIfPresent(Demo03StudentDO::getName, reqVO.getName())
                .eqIfPresent(Demo03StudentDO::getSex, reqVO.getSex())
                .eqIfPresent(Demo03StudentDO::getDescription, reqVO.getDescription())
                .betweenIfPresent(Demo03StudentDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(Demo03StudentDO::getId));
    }

}