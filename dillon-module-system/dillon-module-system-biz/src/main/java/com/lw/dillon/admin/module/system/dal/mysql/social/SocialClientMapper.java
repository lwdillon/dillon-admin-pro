package com.lw.dillon.admin.module.system.dal.mysql.social;

import com.lw.dillon.admin.framework.common.pojo.PageResult;
import com.lw.dillon.admin.framework.mybatis.core.mapper.BaseMapperX;
import com.lw.dillon.admin.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.lw.dillon.admin.module.system.controller.admin.socail.vo.client.SocialClientPageReqVO;
import com.lw.dillon.admin.module.system.dal.dataobject.social.SocialClientDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SocialClientMapper extends BaseMapperX<SocialClientDO> {

    default SocialClientDO selectBySocialTypeAndUserType(Integer socialType, Integer userType) {
        return selectOne(SocialClientDO::getSocialType, socialType,
                SocialClientDO::getUserType, userType);
    }

    default PageResult<SocialClientDO> selectPage(SocialClientPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<SocialClientDO>()
                .likeIfPresent(SocialClientDO::getName, reqVO.getName())
                .eqIfPresent(SocialClientDO::getSocialType, reqVO.getSocialType())
                .eqIfPresent(SocialClientDO::getUserType, reqVO.getUserType())
                .likeIfPresent(SocialClientDO::getClientId, reqVO.getClientId())
                .eqIfPresent(SocialClientDO::getStatus, reqVO.getStatus())
                .orderByDesc(SocialClientDO::getId));
    }

}
